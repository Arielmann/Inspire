package inspire.ariel.inspire.common.di;

import android.support.annotation.Nullable;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.viewutils.ProgressDialogFactory;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuFragmentInjector;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuView;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatMenuComponents;
import lombok.Builder;

@Module
@Builder
public class ViewsModule {

   /*
     * Instantiate startOperations builder and provide only the required view parent
     * in your class, so it may be used as the context for the injected views.
     */
    private static final String TAG = ViewsModule.class.getName();

    @Nullable
    private final ViewsInjector viewsInjector;
    @Nullable
    private final TreatDesignerMenuFragmentInjector treatDesignerMenuFragmentInjector;

   /* @Nullable
    private final SecondaryDrawerItem purchasesHistoryDrawerItem;
    @Nullable
    private final SecondaryDrawerItem logoutDrawerItem;*/

    public ViewsModule(@Nullable ViewsInjector treatsListViewsInjector, @Nullable TreatDesignerMenuFragmentInjector treatDesignerMenuFragmentInjector /*@Nullable SecondaryDrawerItem purchasesHistoryDrawerItem, @Nullable SecondaryDrawerItem logoutDrawerItem*/) {
      /*  if(purchasesHistoryDrawerItem != null || logoutDrawerItem != null){
            Log.e(TAG, "Don't pass values to drawer items in ViewsModule builder. They are there as a workaround due to dagger implementation");
        }*/
        this.viewsInjector = treatsListViewsInjector;
        this.treatDesignerMenuFragmentInjector = treatDesignerMenuFragmentInjector;
        //this.purchasesHistoryDrawerItem = new SecondaryDrawerItem().withIdentifier(AppNumbers.PURCHASE_HISTORY_DRAWER_ITEM_IDENTIFIER).withName(R.string.drawer_item_title_purchases_history);
        //this.logoutDrawerItem = new SecondaryDrawerItem().withIdentifier(AppNumbers.LOGOUT_DRAWER_ITEM_IDENTIFIER).withName(R.string.drawer_item_title_logout);
    }

    @Provides
    List<View> provideQuoteCreatorActivityDisappearingViews() {
        assert viewsInjector != null;
        return new ArrayList<View>() {{
            add(viewsInjector.getAbstractTreatDesignerBinding().bgPicker);
            add(viewsInjector.getAbstractTreatDesignerBinding().postImageBtn);
        }};
    }

    private KProgressHUD newEmptyProgressDialog() {
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

/*    @Provides
    public DrawerFragmentView provideDrawerFragView() {
        assert viewsInjector != null;
        return (DrawerFragmentView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.drawerFragment);
    }*/

    @Provides
    public TreatDesignerMenuView provideTreatDesignerMenuView() {
        assert viewsInjector != null;
        return (TreatDesignerMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.designerMenuFragment);
    }

/*    @Provides
    public TreatListMenuView provideTreatListMenuView() {
        assert viewsInjector != null;
        return (TreatListMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.treatListMenuFragment);
    }*/

    @Provides
    public List<TreatMenuComponents> provideTreatOptionComponents() {
        return new ArrayList<TreatMenuComponents>() {{
            assert treatDesignerMenuFragmentInjector != null;
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatFontImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatFontExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatFontRv));
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatTextSizeImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatTextSizeExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatTextSizeRv));
            add(new TreatMenuComponents(treatDesignerMenuFragmentInjector.getBinding().treatTextColorImgBtn, treatDesignerMenuFragmentInjector.getBinding().treatTextColorExpandingLayout, treatDesignerMenuFragmentInjector.getBinding().treatTextColorRv));
        }};
    }

   /* @Provides
    public Drawer provideDrawer() {
        assert viewsInjector != null;
        return new DrawerBuilder()
                .withActivity(viewsInjector.getActivity())
                .withToolbar(viewsInjector.getDrawerFragmentBinding().toolbar)
                .addDrawerItems(this.purchasesHistoryDrawerItem, this.logoutDrawerItem)
                .build();
    }

    @Provides
    @Named(AppStrings.LOGOUT_DRAWER_ITEM)
    public SecondaryDrawerItem provideLogoutItem() {
        return this.logoutDrawerItem;
    }

    @Provides
    @Named(AppStrings.PURCHASE_HISTORY_DRAWER_ITEM)
    public SecondaryDrawerItem providePurchasesHistoryItemItem() {
        return this.purchasesHistoryDrawerItem;
    }


    @Provides
    @Named(AppStrings.CREATE_TREAT_DRAWER_ITEM)
    public SecondaryDrawerItem provaideCreatTreatDraewrItem() {
        return new SecondaryDrawerItem().withIdentifier(AppNumbers.CREATE_TREAT_DRAWER_ITEM_IDENTIFIER).withName(R.string.drawer_item_title_create_treat);
    }*/
}

