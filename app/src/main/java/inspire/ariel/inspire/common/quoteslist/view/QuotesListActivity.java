package inspire.ariel.inspire.common.quoteslist.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorActivity;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView {

    private String TAG = QuotesListActivity.class.getName();
    private QuoteListPresenterImpl presenter;
    private RecyclerView rv;
    private KProgressHUD progressHUD;
    private ActivityQuoteListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        presenter = new QuoteListPresenterImpl(this, ((InspireApplication) getApplication()).getAppComponent());
        showProgressDialog();
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

    private void initActivity() {
        rv = binding.recyclerView;
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                OrientationHelper.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        rv.setHasFixedSize(true);
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(QuotesListActivity.this, QuotesCreatorActivity.class));
        presenter.retrieveLeaderQuotesFromServer();
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    @Override
    protected void onStart() {
        if (rv != null) {
            presenter.retrieveLeaderQuotesFromServer();
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void presentQuotesOnScreen(QuoteListAdapter adapter) {
        rv.setAdapter(adapter);
        dismissProgressDialog();
    }

    @Override
    public void showQuoteRefreshErrorMessage() {
        Toast.makeText(this, R.string.error_quote_refresh, Toast.LENGTH_LONG).show();
    }

    private void dismissProgressDialog() {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
    }

    @Override
    public void showProgressDialog() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    @Override
    public void showNoInternetConnectionMessage() {
        dismissProgressDialog();
        binding.criticalErrorTV.setVisibility(View.VISIBLE);
    }
}


