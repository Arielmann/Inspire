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

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.events.OnQuotesLoadedEvent;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.databinding.CompoRecyclerViewBinding;

public class QuoteListActivity extends AppCompatActivity implements QuotesListActivityView {

    private String TAG = QuoteListActivity.class.getName();
    private CompoRecyclerViewBinding binding;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.compo_recycler_view);
        rv = binding.recyclerView;
        startQuotesFetching();
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    public void onGroupsLoadedEvent(OnQuotesLoadedEvent event) {
        if (event.quotes.size() == 0) {
            binding.emptyDataSetTV.setVisibility(View.GONE);
            return;
        }
        binding.emptyDataSetTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void presentQuotesOnScreen(QuoteListAdapter adapter) {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                OrientationHelper.VERTICAL);
        rv.addItemDecoration(dividerItemDecoration);
        rv.setHasFixedSize(true);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this, R.string.download_content_error, Toast.LENGTH_LONG).show();
    }

    private void startQuotesFetching(){
        new QuoteListPresenterImpl(((InspireApplication) getApplication()).getAppComponent(), this);
    }
}


