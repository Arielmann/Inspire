package inspire.ariel.inspire.common.di;

import android.support.annotation.Nullable;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.view.optionsmenufragment.TreatListMenuView;
import inspire.ariel.inspire.common.utils.viewutils.ProgressDialogFactory;
import inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment.TreatCreatorMenuFragmentInjector;
import inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment.TreatCreatorMenuView;
import inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment.TreatMenuComponents;
import lombok.Builder;

@Module
@Builder
public class ViewsModule {


    /**
     * Instantiate startOperations builder and provide only the required view parent
     * in your class, so it may be used as the context for the injected views.
     */

    @Nullable
    private ViewInjector viewsInjector;
    @Nullable
    private TreatCreatorMenuFragmentInjector treatCreatorMenuFragmentInjector;

    public ViewsModule(@Nullable ViewInjector treatsListViewInjector, @Nullable TreatCreatorMenuFragmentInjector treatCreatorMenuFragmentInjector) {
        this.viewsInjector = treatsListViewInjector;
        this.treatCreatorMenuFragmentInjector = treatCreatorMenuFragmentInjector;
    }

    @Provides
    List<View> provideQuoteCreatorActivityDisappearingViews() {
        assert viewsInjector != null;
        return new ArrayList<View>() {{
            add(viewsInjector.getActivityTreatCreatorBinding().bgPicker);
            add(viewsInjector.getActivityTreatCreatorBinding().postImageBtn);
        }};
    }

    private KProgressHUD newEmptyProgressDialog(){
        assert viewsInjector != null;
        ProgressDialogFactory factory = new ProgressDialogFactory(viewsInjector.getContext());
        return factory.newInstance();
    }

    @Provides
    @Named(AppStrings.PAGING_TREATS_LIST_PROGRESS_DIALOG)
    public KProgressHUD providePagingQuotesListProgressDialog() {
        return newEmptyProgressDialog();
    }

    @Provides
    @Named(AppStrings.MAIN_PROGRESS_DIALOG)
    public KProgressHUD provideMainQuoteListProgressDialog() {
        assert viewsInjector != null;
        ProgressDialogFactory factory = new ProgressDialogFactory(viewsInjector.getContext());
        return factory.newInstance(viewsInjector.getResources().getString(R.string.please_wait));
    }

    @Provides
    @Named(AppStrings.LOGIN_LOGOUT_PROGRESS_DIALOG)
    public KProgressHUD provideLoginLogoutQuoteListProgressDialog() {
        assert viewsInjector != null;
        ProgressDialogFactory factory = new ProgressDialogFactory(viewsInjector.getContext());
        return factory.newInstance(viewsInjector.getResources().getString(R.string.please_wait));
    }

    @Provides
    public TreatCreatorMenuView provideQuoteCreatorMenuView() {
        assert viewsInjector != null;
        return (TreatCreatorMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.treatCreatorMenuFragment);
    }

    @Provides
    public TreatListMenuView provideQuoteListMenuView() {
        assert viewsInjector != null;
        return (TreatListMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.treatListMenuFragment);
    }

    @Provides
    public List<TreatMenuComponents> provideQuoteOptionComponents() {
        return new ArrayList<TreatMenuComponents>() {{
            assert treatCreatorMenuFragmentInjector != null;
            add(new TreatMenuComponents(treatCreatorMenuFragmentInjector.getBinding().treatFontImgBtn, treatCreatorMenuFragmentInjector.getBinding().treatFontExpandingLayout, treatCreatorMenuFragmentInjector.getBinding().treatFontRv));
            add(new TreatMenuComponents(treatCreatorMenuFragmentInjector.getBinding().treatTextSizeImgBtn, treatCreatorMenuFragmentInjector.getBinding().treatTextSizeExpandingLayout, treatCreatorMenuFragmentInjector.getBinding().treatTextSizeRv));
            add(new TreatMenuComponents(treatCreatorMenuFragmentInjector.getBinding().treatTextColorImgBtn, treatCreatorMenuFragmentInjector.getBinding().treatTextColorExpandingLayout, treatCreatorMenuFragmentInjector.getBinding().treatTextColorRv));
        }};
    }


}

