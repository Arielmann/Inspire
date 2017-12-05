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
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.callbackutils.GenericOperationCallback;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorActivity;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView {

    @Inject
    QuoteListAdapter adapter;

    @Inject
    @Named(AppStrings.QUOTE_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    private String TAG = QuotesListActivity.class.getName();
    private QuoteListPresenter presenter;
    private KProgressHUD mainProgressDialog;
    private KProgressHUD pagingProgressDialog;
    private ActivityQuoteListBinding binding;
    private InspireApplication application;

    /**
     * Initialization
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        showMainProgressDialog();
        application = (InspireApplication) getApplication();
        application.initApp(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                initActivity();
            }

            @Override
            public void onFailure() {
                //TODO: if local db support available, load it instead of presenting error
                showNoInternetConnectionMessage();
                binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_restart), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        presenter.OnNewIntent(intent);
        super.onNewIntent(intent);
    }

    private void initActivity() {
        application.getAppComponent().inject(this);
        presenter = new QuoteListPresenterImpl(((InspireApplication) getApplication()).getAppComponent(), this, adapter);
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
     * Menu
     */

    /**
     * Progress Dialog
     */
    @Override
    public void showMainProgressDialog() {
        //TODO: find a more UX friendly solution
        mainProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(false)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_DIM_ANIMATION_SPEED)
                .setDimAmount(AppNumbers.MAIN_PROGRESS_DIALOG_DIM_AMOUNT)
                .show();
    }

    @Override
    public void showPagingProgressDialog() {
        pagingProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(AppNumbers.PAGING_PROGRESS_DIALOG_DIM_AMOUNT)
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_DIM_ANIMATION_SPEED)
                .show();
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
     * Show messages
     */
    //Todo: Implement fragment error message with event listening or find some other solution
    @Override
    public void showNoInternetConnectionMessage() {
        dismissMainProgressDialog();
        Toast.makeText(this, getResources().getString(R.string.error_no_connection), Toast.LENGTH_LONG).show();
        //binding.criticalErrorTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void showQuoteRefreshErrorMessage() {
        Toast.makeText(this, R.string.error_quote_refresh, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoQuotesMessage() {
        Toast.makeText(this, getResources().getString(R.string.error_no_quotes), Toast.LENGTH_LONG).show();
    }

    /**
     * Getters
     */
    @Override
    public DiscreteScrollView getQuotesListRv() {
        return binding.quotesRv;
    }

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



