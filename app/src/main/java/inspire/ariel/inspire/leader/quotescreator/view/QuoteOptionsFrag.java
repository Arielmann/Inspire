package inspire.ariel.inspire.leader.quotescreator.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.databinding.FragQuoteOptionsBinding;

public class QuoteOptionsFrag extends Fragment implements QuoteOptionsView {

    private static final String TAG = QuoteOptionsFrag.class.getSimpleName();
    private FragQuoteOptionsBinding binding;
    private QuotesCreatorViewForFragments quotesCreatorActivityView;
    private List<QuoteOptionComponents> quoteOptionComponents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_quote_options, container, false);
        initQuoteOptionComponents();
        initExpandableLayouts();
        initImgButtons();
        initRecyclerView(binding.quoteFontRV, AppInts.THREE_SPAN_COUNT);
        initRecyclerView(binding.quoteTextSizeRV, AppInts.SEVEN_SPAN_COUNT);
        initRecyclerView(binding.quoteTextColorRV, AppInts.SEVEN_SPAN_COUNT);
        return binding.getRoot();
    }

    private void initExpandableLayouts() {
        for (QuoteOptionComponents components : quoteOptionComponents) {
            components.getExpandableLayout().collapse();
        }
    }

    private void initQuoteOptionComponents() {
        quoteOptionComponents = new ArrayList<QuoteOptionComponents>() {{
            add(new QuoteOptionComponents(binding.quoteFontImgBtn, binding.quoteFontExpandingLayout, binding.quoteFontRV));
            add(new QuoteOptionComponents(binding.quoteTextSizeImgBtn, binding.quoteTextSizeExpandingLayout, binding.quoteTextSizeRV));
            add(new QuoteOptionComponents(binding.quoteTextColorImgBtn, binding.quoteTextColorExpandingLayout, binding.quoteTextColorRV));
        }};

    }

    private final View.OnClickListener onOptionImageClickedClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (QuoteOptionComponents components : quoteOptionComponents) {
                if (components.getImageButton().equals(view) && !components.getExpandableLayout().isExpanded()) {
                    components.getExpandableLayout().expand();
                    Log.i(TAG, quoteOptionComponents.toString() + " has expanded");
                } else {
                    components.getExpandableLayout().collapse();
                }
            }
            quotesCreatorActivityView.refreshCurrentBackground(); //Required to prevent keyboard show/hide unexpected bugs after option button clicked
        }
    };

    @Override
    public void setQuotesCreatorActivityView(QuotesCreatorViewForFragments quotesCreatorActivityView) {
        this.quotesCreatorActivityView = quotesCreatorActivityView;
    }

    private void initImgButtons() {
        binding.quoteFontImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.quoteTextSizeImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.quoteTextColorImgBtn.setOnClickListener(onOptionImageClickedClicked);
    }

    private void initRecyclerView(RecyclerView rv, int spanCount) {
        RecyclerView.LayoutManager lm = new GridLayoutManager(getActivity(), spanCount);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
    }

    @Override
    public RecyclerView getQuoteTextSizesRV() {
        return binding.quoteTextSizeRV;
    }

    @Override
    public RecyclerView getQuoteTextColorRV() {
        return binding.quoteTextColorRV;
    }

    @Override
    public RecyclerView getFontsRV() {
        return binding.quoteFontRV;
    }
}