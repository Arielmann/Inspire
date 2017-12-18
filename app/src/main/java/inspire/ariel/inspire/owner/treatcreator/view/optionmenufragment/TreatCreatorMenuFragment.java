package inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment;

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
import inspire.ariel.inspire.common.di.RecyclerViewsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.databinding.FragmentTreatCreatorMenuBinding;
import inspire.ariel.inspire.owner.treatcreator.adapters.FontsAdapter;
import inspire.ariel.inspire.owner.treatcreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.owner.treatcreator.adapters.TextSizesAdapter;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewController;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewTreatProps;
import lombok.Getter;
import lombok.Setter;

public class TreatCreatorMenuFragment extends Fragment implements TreatCreatorMenuView, TreatCreatorMenuFragmentInjector {

    @Inject //ResourcesModule
    ResourcesProvider customResourcesProvider;

    @Inject //ViewsModule
    List<TreatMenuComponents> treatOptionComponents;

    private static final String TAG = TreatCreatorMenuFragment.class.getSimpleName();
    @Getter private FragmentTreatCreatorMenuBinding binding;
    @Setter private TreatsCreatorViewTreatProps treatsCreatorActivityViewProps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_treat_creator_menu, container, false);
        return binding.getRoot();
    }

    @Override
    public void willInject(AppComponent component, TreatsCreatorViewController controller, AssetManager assetManager) {
        DaggerViewComponent.builder()
                .appModule(new AppModule(null))
                .recyclerViewsModule(new RecyclerViewsModule())
                .presentersModule(PresentersModule.builder().appComponent(component).treatsCreatorViewController(controller).build())
                .resourcesModule(new ResourcesModule(getResources(), assetManager))
                .viewsModule(ViewsModule.builder().treatCreatorMenuFragmentInjector(this).build())
                .build()
                .inject(this);
    }

    @Override
    public void initView() {
        initExpandableLayouts();
        initImgButtons();
        initRecyclerViews();
    }


    private void initRecyclerViews() {

        FontsAdapter fontsAdapter = new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            treatsCreatorActivityViewProps.setTreatFont(font);
            treatsCreatorActivityViewProps.refreshCurrentBackground(); //Required to prevent background changes bug
            treatsCreatorActivityViewProps.getPresenter().onTreatFontClicked(font.getPath());
        });

        TextSizesAdapter textSizeAdapter = new TextSizesAdapter(customResourcesProvider.getFontsSizes(), textSize -> {
            treatsCreatorActivityViewProps.setTreatTextSize(textSize.getSize());
            treatsCreatorActivityViewProps.refreshCurrentBackground(); //Required to prevent background changes bug
        });

        TextColorsAdapter textColorsAdapter = new TextColorsAdapter(customResourcesProvider.getColors(), color -> treatsCreatorActivityViewProps.setTreatTextColor(color));

        initRecyclerView(binding.treatFontRv, AppNumbers.THREE_SPAN_COUNT, fontsAdapter);
        initRecyclerView(binding.treatTextSizeRv, AppNumbers.SEVEN_SPAN_COUNT, textSizeAdapter);
        initRecyclerView(binding.treatTextColorRv, AppNumbers.SEVEN_SPAN_COUNT, textColorsAdapter);
    }

    private void initExpandableLayouts() {
        for (TreatMenuComponents components : treatOptionComponents) {
            components.getExpandableLayout().collapse();
        }
    }

    private final View.OnClickListener onOptionImageClickedClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (TreatMenuComponents components : treatOptionComponents) {
                if (components.getImageButton().equals(view) && !components.getExpandableLayout().isExpanded()) {
                    components.getExpandableLayout().expand();
                    Log.i(TAG, treatOptionComponents.toString() + " has expanded");
                } else {
                    components.getExpandableLayout().collapse();
                }
            }
            treatsCreatorActivityViewProps.refreshCurrentBackground(); //Required to prevent keyboard show/hide unexpected bugs after option button clicked
        }
    };

    private void initImgButtons() {
        binding.treatFontImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.treatTextSizeImgBtn.setOnClickListener(onOptionImageClickedClicked);
        binding.treatTextColorImgBtn.setOnClickListener(onOptionImageClickedClicked);
    }

    private void initRecyclerView(RecyclerView rv, int spanCount, RecyclerView.Adapter adapter) {
        RecyclerView.LayoutManager lm = new GridLayoutManager(getActivity(), spanCount);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
    }

    @Override
    public void collapseAllOptionLayouts() {
        for (TreatMenuComponents components : treatOptionComponents) {
            components.getExpandableLayout().collapse();
        }
        treatsCreatorActivityViewProps.refreshCurrentBackground(); //Required to prevent unexpected background disappearing bug after option button clicked
    }


    @Override
    public boolean areAllOptionLayoutsCollapsed() {
        for (TreatMenuComponents components : treatOptionComponents) {
            if (components.getExpandableLayout().isExpanded()) {
                return false;
            }
        }
        return true;
    }
}