package inspire.ariel.inspire.common.quoteslist.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.databinding.ActivityQuoteListBinding;
import inspire.ariel.inspire.leader.quotecreatorfrag.view.QuotesCreatorActivity;

public class QuotesListActivity extends AppCompatActivity implements QuotesListView {

    private String TAG = QuotesListActivity.class.getName();
    private ActivityQuoteListBinding binding;
    private QuoteListPresenterImpl presenter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list);
        rv = binding.recyclerView;
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                OrientationHelper.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        rv.setHasFixedSize(true);
        startQuotesFetching();
        binding.goToCreateQuoteActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(QuotesListActivity.this, QuotesCreatorActivity.class));
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void startQuotesFetching() {
        //Download quotes server call is done on presenter
        presenter = new QuoteListPresenterImpl(this, ((InspireApplication) getApplication()).getAppComponent());
    }

    @Override
    public void presentQuotesOnScreen(QuoteListAdapter adapter) {
        rv.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this, R.string.download_content_error, Toast.LENGTH_LONG).show();
    }

}


