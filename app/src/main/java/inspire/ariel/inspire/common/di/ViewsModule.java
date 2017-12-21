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
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuFragmentInjector;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuView;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatMenuComponents;
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
    private TreatDesignerMenuFragmentInjector treatDesignerMenuFragmentInjector;

    public ViewsModule(@Nullable ViewInjector treatsListViewInjector, @Nullable TreatDesignerMenuFragmentInjector treatDesignerMenuFragmentInjector) {
        this.viewsInjector = treatsListViewInjector;
        this.treatDesignerMenuFragmentInjector = treatDesignerMenuFragmentInjector;
    }

    @Provides
    List<View> provideQuoteCreatorActivityDisappearingViews() {
        assert viewsInjector != null;
        return new ArrayList<View>() {{
            add(viewsInjector.getAbstractTreatDesignerBinding().bgPicker);
            add(viewsInjector.getAbstractTreatDesignerBinding().postImageBtn);
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
    public TreatDesignerMenuView provideQuoteCreatorMenuView() {
        assert viewsInjector != null;
        return (TreatDesignerMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.designerMenuFragment);
    }

    @Provides
    public TreatListMenuView provideQuoteListMenuView() {
        assert viewsInjector != null;
        return (TreatListMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.treatListMenuFragment);
    }

    @Provides
    public List<TreatMenuComponents> provideQuoteOptionComponents() {
        return new ArrayList<TreatMenuComponents>() {{
            assert treatDesignerMenuFragmentInjector != null;
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatFontImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatFontExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatFontRv));
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatTextSizeImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatTextSizeExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatTextSizeRv));
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatTextColorImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatTextColorExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatTextColorRv));
        }};
    }
}

