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
import inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment.QuoteListMenuView;
import inspire.ariel.inspire.common.utils.viewutils.ProgressDialogFactory;
import inspire.ariel.inspire.owner.quotecreator.view.optionmenufragment.QuoteCreatorMenuFragmentInjector;
import inspire.ariel.inspire.owner.quotecreator.view.optionmenufragment.QuoteCreatorMenuView;
import inspire.ariel.inspire.owner.quotecreator.view.optionmenufragment.QuoteMenuComponents;
import lombok.Builder;

@Module
@Builder
public class ViewsModule {


    /**
     * Instantiate with builder and provide only the required view parent
     * in your class, so it may be used as the context for the injected views.
     */

    @Nullable
    private ViewInjector viewsInjector;
    @Nullable
    private QuoteCreatorMenuFragmentInjector quoteCreatorMenuFragmentInjector;

    public ViewsModule(@Nullable ViewInjector quotesListViewInjector, @Nullable QuoteCreatorMenuFragmentInjector quoteCreatorMenuFragmentInjector) {
        this.viewsInjector = quotesListViewInjector;
        this.quoteCreatorMenuFragmentInjector = quoteCreatorMenuFragmentInjector;
    }

    @Provides
    List<View> provideQuoteCreatorActivityDisappearingViews() {
        assert viewsInjector != null;
        return new ArrayList<View>() {{
            add(viewsInjector.getBinding().bgPicker);
            add(viewsInjector.getBinding().postImageBtn);
        }};
    }

    private KProgressHUD newEmptyProgressDialog(){
        assert viewsInjector != null;
        ProgressDialogFactory factory = new ProgressDialogFactory(viewsInjector.getContext());
        return factory.newInstance();
    }

    @Provides
    @Named(AppStrings.PAGING_QUOTES_LIST_PROGRESS_DIALOG)
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
    public QuoteCreatorMenuView provideQuoteCreatorMenuView() {
        assert viewsInjector != null;
        return (QuoteCreatorMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.quoteCreatorMenuFragment);
    }

    @Provides
    public QuoteListMenuView provideQuoteListMenuView() {
        assert viewsInjector != null;
        return (QuoteListMenuView) viewsInjector.getSupportFragmentManager().findFragmentById(R.id.quoteListMenuFragment);
    }

    @Provides
    public List<QuoteMenuComponents> provideQuoteOptionComponents() {
        return new ArrayList<QuoteMenuComponents>() {{
            assert quoteCreatorMenuFragmentInjector != null;
            add(new QuoteMenuComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteFontImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteFontExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteFontRv));
            add(new QuoteMenuComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeRv));
            add(new QuoteMenuComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorRv));
        }};
    }


}

