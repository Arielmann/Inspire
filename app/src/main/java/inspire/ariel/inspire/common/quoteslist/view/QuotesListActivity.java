package inspire.ariel.inspire.common.quoteslist.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;
import inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment.QuoteListMenuView;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.backendutils.CheckLoggedInCallback;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorActivity;
import lombok.Getter;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView, QuoteListViewInjector {

    @Inject //List Module
            QuoteListAdapter adapter;

    @Inject //Views Module
    @Named(AppStrings.QUOTE_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject //Views Module
    @Named(AppStrings.MAIN_QUOTES_LIST_PROGRESS_DIALOG)
    KProgressHUD mainProgressDialog;

    @Inject //Views Module
    @Named(AppStrings.PAGING_QUOTES_LIST_PROGRESS_DIALOG)
    KProgressHUD pagingProgressDialog;

    @Inject //Presenters Module
    @Getter
    QuoteListPresenter presenter;

    @Inject
    QuoteListMenuView quoteListMenuView;

    private String TAG = QuotesListActivity.class.getName();
    private ActivityQuoteListBinding binding;
    private InspireApplication application;

    /**
     * Initialization
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        inject();
        showMainProgressDialog();
        application = (InspireApplication) getApplication();
        application.initApp(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                initActivity();
            }

            @Override
            public void onFailure(String reason) {
                //TODO: if local db support available, load it instead of presenting error
                Log.e(TAG, "Error registering device: " + reason);
                dismissMainProgressDialog();
                showToastErrorMessage(getResources().getString(R.string.error_no_connection));
                binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_restart), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        presenter.OnNewIntent(intent);
        super.onNewIntent(intent);
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule((InspireApplication) getApplication()))
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).quotesListView(this).build())
                .viewsModule(ViewsModule.builder().quotesListViewInjector(this).build())
                .listsModule(new ListsModule())
                .build()
                .inject(this);
    }

    private void initActivity() {
        quoteListMenuView.init(this);
        //TODO: You can't know who will finish first, therefore, dismiss progress dialog only after both finish
        presenter.init(adapter);
        presenter.checkIfUserLoggedIn(checkLoggedInCallback);
        initQuotesRecyclerView();
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(QuotesListActivity.this, QuotesCreatorActivity.class));
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    private void initQuotesRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, OrientationHelper.VERTICAL);
        binding.quotesRv.addItemDecoration(dividerItemDecoration);
        binding.quotesRv.setHasFixedSize(discreteScrollViewData.isHasFixedSize());
        binding.quotesRv.setSlideOnFling(discreteScrollViewData.isSetSlideOnFling());
        binding.quotesRv.setAdapter(adapter);
        binding.quotesRv.addScrollStateChangeListener(presenter.getOnScrollChangedListener());
        binding.quotesRv.setOrientation(discreteScrollViewData.getOrientation());
        binding.quotesRv.setItemTransitionTimeMillis(discreteScrollViewData.getTimeMillis());
        binding.quotesRv.setOffscreenItems(discreteScrollViewData.getOffScreenItems());
        binding.quotesRv.setItemTransformer(discreteScrollViewData.getScaleTransformer());
    }

    /**
     * Optional Life Cycle Methods
     */

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * User login status adaptation
     */

    private CheckLoggedInCallback checkLoggedInCallback = new CheckLoggedInCallback() {
        @Override
        public void onUserStatusReceived(boolean isLoggedIn) {
            Log.i(TAG, "Login status check completed. Is user logged in? " + isLoggedIn);
            if (isLoggedIn) {
                quoteListMenuView.showLoginBtn();
                quoteListMenuView.setLogoutAvailable();
                return;
            }
            quoteListMenuView.showLoginBtn();
            quoteListMenuView.setLoginAvailable();
            binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_restart), Toast.LENGTH_SHORT).show());
        }


        //Todo: check if you crush when trying to log in with an already logged in user
        @Override
        public void onFailure(String reason) {
            quoteListMenuView.showLoginBtn();
            quoteListMenuView.setLoginAvailable();
            Log.e(TAG, "Error in login validation. Reason: " + reason + " making login available");
        }
    };

    /**
     * Progress Dialog
     */
    @Override
    public void showMainProgressDialog() {
        //TODO: find a more UX friendly solution
        mainProgressDialog.show();
    }

    @Override
    public void showPagingProgressDialog() {
        pagingProgressDialog.show();
    }

    @Override
    public void dismissMainProgressDialog() {
        if (mainProgressDialog != null) {
            mainProgressDialog.dismiss();
        }
    }

    @Override
    public void dismissPagingProgressDialog() {
        if (pagingProgressDialog != null) {
            pagingProgressDialog.dismiss();
        }
    }


    /**
     * Show Messages
     */

    @Override
    public void showToastErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServerOperationFailed(String error) {
        dismissMainProgressDialog();
        showToastErrorMessage(error);
    }

    /**
     * Getters
     */

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * DiscreteScrollView Programmatic Scrolling
     */

    @Override
    public void scrollQuoteListToTop() {
        binding.quotesRv.scrollToPosition(0);
    }
}



