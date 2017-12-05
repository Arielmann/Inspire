package inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment;

import android.content.res.AssetManager;
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

import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.databinding.FragmentQuoteCreatorMenuBinding;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextSizesAdapter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorViewController;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorViewQuoteProperties;
import lombok.Getter;
import lombok.Setter;

public class QuoteCreatorMenuFragment extends Fragment implements QuoteCreatorMenuView, QuoteCreatorMenuFragmentInjector {

    @Inject //ResourcesModule
    ResourcesProvider customResourcesProvider;

    @Inject //PresentersModule
    QuotesCreatorPresenter presenter;

    @Inject //ViewsModule
    List<QuoteOptionComponents> quoteOptionComponents;

    private static final String TAG = QuoteCreatorMenuFragment.class.getSimpleName();
    @Getter private FragmentQuoteCreatorMenuBinding binding;
    @Setter private QuotesCreatorViewQuoteProperties quotesCreatorActivityViewProperties;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_creator_menu, container, false);
        return binding.getRoot();
    }

    @Override
    public void willInject(AppComponent component, QuotesCreatorViewController controller, AssetManager assetManager){
        DaggerViewComponent.builder()
                .appModule(new AppModule(null))
                .listsModule(new ListsModule())
                .presentersModule(PresentersModule.builder().appComponent(component).quotesCreatorViewController(controller).build())
                .resourcesModule(new ResourcesModule(getResources(), assetManager))
                .viewsModule(ViewsModule.builder().quoteCreatorMenuFragmentInjector(this).build())
                .build()
                .inject(this);
    }

    @Override
    public void initView(){
        initExpandableLayouts();
        initImgButtons();
        initRecyclerViews();
    }

    private void initRecyclerViews() {

       FontsAdapter fontsAdapter = new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            quotesCreatorActivityViewProperties.setQuoteFont(font);
            quotesCreatorActivityViewProperties.refreshCurrentBackground(); //Required to prevent background changes bug
            presenter.onQuoteFontClicked(font.getPath());
        });

       TextSizesAdapter textSizeAdapter = new TextSizesAdapter(customResourcesProvider.getFontsSizes(), textSize -> {
            quotesCreatorActivityViewProperties.setQuoteTextSize(textSize.getSize());
            quotesCreatorActivityViewProperties.refreshCurrentBackground(); //Required to prevent background changes bug
        });

       TextColorsAdapter textColorsAdapter = new TextColorsAdapter(customResourcesProvider.getColors(), color -> quotesCreatorActivityViewProperties.setQuoteTextColor(color));

        initRecyclerView(binding.quoteFontRv, AppNumbers.THREE_SPAN_COUNT, fontsAdapter);
        initRecyclerView(binding.quoteTextSizeRv, AppNumbers.SEVEN_SPAN_COUNT, textSizeAdapter);
        initRecyclerView(binding.quoteTextColorRv, AppNumbers.SEVEN_SPAN_COUNT, textColorsAdapter);
    }

    private void initExpandableLayouts() {
        for (QuoteOptionComponents components : quoteOptionComponents) {
            components.getExpandableLayout().collapse();
        }
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
            quotesCreatorActivityViewProperties.refreshCurrentBackground(); //Required to prevent keyboard show/hide unexpected bugs after option button clicked
        }
    };

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
        quotesCreatorActivityViewProperties.refreshCurrentBackground(); //Required to prevent unexpected background disappearing bug after option button clicked
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