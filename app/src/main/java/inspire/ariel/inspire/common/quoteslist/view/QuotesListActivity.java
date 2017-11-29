package inspire.ariel.inspire.common.quoteslist.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorActivity;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView {

    private String TAG = QuotesListActivity.class.getName();
    private QuoteListPresenterImpl presenter;
    private KProgressHUD mainProgressDialog;
    private KProgressHUD pagingProgressDialog;
    private ActivityQuoteListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        showMainProgressDialog();
        InspireApplication application = (InspireApplication) getApplication();
        application.initApp(new ContinuousOperationCallback() {
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
        initQuotesRecyclerView();
        presenter = new QuoteListPresenterImpl(this, ((InspireApplication) getApplication()).getAppComponent());
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(QuotesListActivity.this, QuotesCreatorActivity.class));
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    private void initQuotesRecyclerView(){
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.quotesRv.getContext(), OrientationHelper.VERTICAL);
        binding.quotesRv.addItemDecoration(dividerItemDecoration);
        binding.quotesRv.setHasFixedSize(true);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void showQuoteRefreshErrorMessage() {
        Toast.makeText(this, R.string.error_quote_refresh, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMainProgressDialog() {
    //TODO: find a more UX friendly solution
        mainProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(false)
                .setAnimationSpeed(AppInts.PROGRESS_DIALOG_DIM_ANIMATION_SPEED)
                .setDimAmount(AppInts.MAIN_PROGRESS_DIALOG_DIM_AMOUNT)
                .show();
    }

    @Override
    public void showPagingProgressDialog() {
        pagingProgressDialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(AppInts.PAGING_PROGRESS_DIALOG_DIM_AMOUNT)
                .setCancellable(true)
                .setAnimationSpeed(AppInts.PROGRESS_DIALOG_DIM_ANIMATION_SPEED)
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

    @Override
    public void showNoInternetConnectionMessage() {
        dismissMainProgressDialog();
        binding.criticalErrorTV.setVisibility(View.VISIBLE);
    }

    @Override
    public DiscreteScrollView getQuotesListRv() {
        return binding.quotesRv;
    }

    @Override
    public void scrollQuoteListToTop() {
        binding.quotesRv.scrollToPosition(AppInts.ZERO);
    }
}


