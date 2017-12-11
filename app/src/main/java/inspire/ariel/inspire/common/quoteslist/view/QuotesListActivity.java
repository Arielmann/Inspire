package inspire.ariel.inspire.common.quoteslist.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashSet;
import java.util.Set;

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
import inspire.ariel.inspire.common.di.ViewInjector;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;
import inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment.QuoteListMenuView;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity.QuotesCreatorActivity;
import lombok.Getter;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView, ViewInjector {

    @Inject //List Module
            QuoteListAdapter adapter;

    @Inject //Views Module
    @Named(AppStrings.QUOTE_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject //Views Module
    @Named(AppStrings.MAIN_PROGRESS_DIALOG)
    @Getter
    KProgressHUD mainProgressDialog;

    @Inject //Views Module
    @Named(AppStrings.PAGING_QUOTES_LIST_PROGRESS_DIALOG)
    @Getter
    KProgressHUD pagingProgressDialog;

    @Inject //Views Module
    @Named(AppStrings.LOGIN_LOGOUT_PROGRESS_DIALOG)
    @Getter
    KProgressHUD loginLogoutProgressDialog;

    @Inject //Presenters Module
    @Getter
    QuoteListPresenter presenter;

    @Inject //Views Module
            QuoteListMenuView quoteListMenuView;

    private String TAG = QuotesListActivity.class.getName();
    private ActivityQuoteListBinding binding;
    private InspireApplication application;
    private Set<KProgressHUD> activeProgressDialogs;

    /**
     * Initialization
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        inject();
        activeProgressDialogs = new HashSet<>();
        showProgressDialog(mainProgressDialog);
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
                dismissProgressDialog(mainProgressDialog);
                showToastErrorMessage(getResources().getString(R.string.error_no_connection));
                binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_login), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule((InspireApplication) getApplication()))
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).quotesListView(this).build())
                .viewsModule(ViewsModule.builder().viewsInjector(this).build())
                .listsModule(new ListsModule())
                .build()
                .inject(this);
    }

    private void initActivity() {
        quoteListMenuView.init(this);
        //TODO: You can't know between quotes download or login check - who will finish first. Therefore, dismiss progress dialog only after both finish
        presenter.init(adapter);
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
    protected void onStart() {
        presenter.onStart();
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        presenter.OnNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

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


    @Override
    public void onUserLoggedIn() {
        dismissProgressDialog(loginLogoutProgressDialog);
        binding.goToCreateQuoteActivityBtn.setVisibility(View.VISIBLE);
        quoteListMenuView.resetLoginLogoutBtn(getResources().getDrawable(R.drawable.logout_icon), quoteListMenuView.getOnLogoutClicked());
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(QuotesListActivity.this, QuotesCreatorActivity.class));
    }

    @Override
    public void onUserLoggedOut() {
        dismissProgressDialog(loginLogoutProgressDialog);
        binding.goToCreateQuoteActivityBtn.setVisibility(View.GONE);
        quoteListMenuView.resetLoginLogoutBtn(getResources().getDrawable(R.drawable.login_icon), quoteListMenuView.getOnLoginClicked());
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_login), Toast.LENGTH_SHORT).show());
    }

    /**
     * Progress Dialog
     */

    @Override
    public void showProgressDialog(KProgressHUD dialog) {
        if (dialog != null) {
            activeProgressDialogs.add(dialog);
            dialog.show();
        }
    }

    @Override
    public void dismissProgressDialog(KProgressHUD dialog) {
        if (dialog != null) {
            activeProgressDialogs.remove(dialog);
            dialog.dismiss();
        }
    }

    private void dismissAllProgressDialogs() {
        for (KProgressHUD dialog : activeProgressDialogs) {
            dismissProgressDialog(dialog);
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
        dismissAllProgressDialogs();
        showToastErrorMessage(error);
    }

    @Override
    public void showReallyDeleteDialog(int quotePosition) {
        new MaterialDialog.Builder(this)
                .title(R.string.are_you_sure_title)
                .titleGravity(GravityEnum.CENTER)
                .content(getResources().getString(R.string.really_delete_msg))
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.delete_title)
                .onPositive((dialog, which) -> {
                    showProgressDialog(mainProgressDialog);
                    presenter.deleteQuote(quotePosition);
                })
                .negativeText(R.string.cancel)
                .onNegative((dialog, which) -> dialog.cancel())
                .cancelable(true)
                .show();
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



