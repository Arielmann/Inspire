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

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.databinding.FragmentQuoteCreatorMenuBinding;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextSizesAdapter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;

public class QuoteCreatorMenuFragment extends Fragment implements QuoteCreatorMenuView {

    @Inject
    ResourcesProvider customResourcesProvider;

    private static final String TAG = QuoteCreatorMenuFragment.class.getSimpleName();
    private FragmentQuoteCreatorMenuBinding binding;
    private QuotesCreatorViewForFragments quotesCreatorActivityView;
    private List<QuoteOptionComponents> quoteOptionComponents;
    private QuotesCreatorPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_creator_menu, container, false);
        ((InspireApplication) getActivity().getApplication()).getAppComponent().inject(this);
        initQuoteOptionComponents();
        initExpandableLayouts();
        initImgButtons();
        initRecyclerViews();
        return binding.getRoot();
    }

    private void initRecyclerViews() {

       FontsAdapter fontsAdapter = new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            quotesCreatorActivityView.setQuoteFont(font);
            quotesCreatorActivityView.setBackground(customResourcesProvider.getResources().getDrawable(presenter.getBgDrawableIntValue())); //Required to prevent background changes bug
            presenter.onQuoteFontClicked(font.getPath());
        });

       TextSizesAdapter textSizeAdapter = new TextSizesAdapter(customResourcesProvider.getFontsSizes(), textSize -> {
            quotesCreatorActivityView.setQuoteTextSize(textSize.getSize());
            quotesCreatorActivityView.setBackground(customResourcesProvider.getResources().getDrawable(presenter.getBgDrawableIntValue())); //Required to prevent background changes bug
        });

       TextColorsAdapter textColorsAdapter = new TextColorsAdapter(customResourcesProvider.getColors(), color -> quotesCreatorActivityView.setQuoteTextColor(color));

        initRecyclerView(binding.quoteFontRv, AppNumbers.THREE_SPAN_COUNT, fontsAdapter);
        initRecyclerView(binding.quoteTextSizeRv, AppNumbers.SEVEN_SPAN_COUNT, textSizeAdapter);
        initRecyclerView(binding.quoteTextColorRv, AppNumbers.SEVEN_SPAN_COUNT, textColorsAdapter);
    }

    private void initExpandableLayouts() {
        for (QuoteOptionComponents components : quoteOptionComponents) {
            components.getExpandableLayout().collapse();
        }
    }

    private void initQuoteOptionComponents() {
        quoteOptionComponents = new ArrayList<QuoteOptionComponents>() {{
            add(new QuoteOptionComponents(binding.quoteFontImgBtn, binding.quoteFontExpandingLayout, binding.quoteFontRv));
            add(new QuoteOptionComponents(binding.quoteTextSizeImgBtn, binding.quoteTextSizeExpandingLayout, binding.quoteTextSizeRv));
            add(new QuoteOptionComponents(binding.quoteTextColorImgBtn, binding.quoteTextColorExpandingLayout, binding.quoteTextColorRv));
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
    public RecyclerView getFontsRv() {
        return binding.quoteFontRv;
    }

    @Override
    public RecyclerView getTextColorRv() {
        return binding.quoteTextColorRv;
    }

    @Override
    public RecyclerView getTextSizesRv() {
        return binding.quoteTextSizeRv;
    }

    @Override
    public void setQuotesCreatorActivityView(QuotesCreatorViewForFragments quotesCreatorActivityView) {
        this.quotesCreatorActivityView = quotesCreatorActivityView;
    }

    private void initImgButtons() {
        binding.quoteFontImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.quoteTextSizeImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.quoteTextColorImgBtn.setOnClickListener(onOptionImageClickedClicked);
    }

    private void initRecyclerView(RecyclerView rv, int spanCount, RecyclerView.Adapter adapter) {
        RecyclerView.LayoutManager lm = new GridLayoutManager(getActivity(), spanCount);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
    }

    @Override
    public void collapseAllOptionLayouts() {
        for (QuoteOptionComponents components : quoteOptionComponents) {
            components.getExpandableLayout().collapse();
        }
        quotesCreatorActivityView.refreshCurrentBackground(); //Required to prevent unexpected background disappearing bug after option button clicked
    }


    @Override
    public boolean areAllOptionLayoutsCollapsed() {
        for (QuoteOptionComponents components : quoteOptionComponents) {
            if (components.getExpandableLayout().isExpanded()) {
                return false;
            }
        }
        return true;
    }
}