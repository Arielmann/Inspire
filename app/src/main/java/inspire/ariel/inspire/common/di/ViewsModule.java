package inspire.ariel.inspire.common.di;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.quoteslist.view.QuoteListViewInjector;
import inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment.QuoteListMenuView;
import inspire.ariel.inspire.common.utils.animationutils.AnimatedSlidingView;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorActivityInjector;
import inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment.QuoteCreatorMenuFragmentInjector;
import inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment.QuoteCreatorMenuView;
import inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment.QuoteOptionComponents;
import lombok.Builder;

@Module
@Builder
public class ViewsModule {

    private QuotesCreatorActivityInjector quotesCreatorViewInjector;
    private QuoteListViewInjector quotesListViewInjector;
    private QuoteCreatorMenuFragmentInjector quoteCreatorMenuFragmentInjector;

    public ViewsModule(QuotesCreatorActivityInjector quotesCreatorViewInjector, QuoteListViewInjector quotesListViewInjector, QuoteCreatorMenuFragmentInjector quoteCreatorMenuFragmentInjector) {
        this.quotesCreatorViewInjector = quotesCreatorViewInjector;
        this.quotesListViewInjector = quotesListViewInjector;
        this.quoteCreatorMenuFragmentInjector = quoteCreatorMenuFragmentInjector;
    }

    @Provides
    List<AnimatedSlidingView> provideQuoteCreatorActivityDisappearingViews(){
        AnimatedSlidingView slidingBgPicker = AnimatedSlidingView.builder()
                .view(quotesCreatorViewInjector.getBinding().bgPicker)
                .initialYPos(quotesCreatorViewInjector.getBinding().bgPicker.getTranslationY())
                .endAnimatedYPos(quotesCreatorViewInjector.getBinding().bgPicker.getTranslationY() * Percentages.FIVE_HUNDRED)
                .build();

        AnimatedSlidingView slidingPostImageView = (AnimatedSlidingView.builder()
                .view(quotesCreatorViewInjector.getBinding().postImageBtn)
                .initialYPos(quotesCreatorViewInjector.getBinding().postImageBtn.getTranslationY())
                .endAnimatedYPos(quotesCreatorViewInjector.getBinding().postImageBtn.getTranslationY() * Percentages.FIVE_HUNDRED)
                .build());

        return new ArrayList<AnimatedSlidingView>() {{
            add(slidingBgPicker);
            add(slidingPostImageView);
        }};
    }

    @Provides
    @Named(AppStrings.QUOTES_CREATOR_PROGRESS_DIALOG)
    public KProgressHUD provideQuotesCreatorProgressDialog() {
        return KProgressHUD.create(quotesCreatorViewInjector.getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(quotesCreatorViewInjector.getResources().getString(R.string.please_wait))
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.QUOTE_PROGRESS_HUD_ANIM_SPEED)
                .setDimAmount(AppNumbers.PROGRESS_DIALOG_DIM_AMOUNT);
    }

    @Provides
    @Named(AppStrings.PAGING_QUOTES_LIST_PROGRESS_DIALOG)
    public KProgressHUD provideQuotesListPagingProgressDialog() {
        return KProgressHUD.create(quotesListViewInjector.getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(AppNumbers.PAGING_PROGRESS_DIALOG_DIM_AMOUNT)
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_DIM_ANIMATION_SPEED);
    }

    @Provides
    @Named(AppStrings.MAIN_QUOTES_LIST_PROGRESS_DIALOG)
    public KProgressHUD provideQuoteListMainProgressDialog() {
        return KProgressHUD.create(quotesListViewInjector.getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(AppNumbers.PAGING_PROGRESS_DIALOG_DIM_AMOUNT)
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_DIM_ANIMATION_SPEED);
    }

    @Provides
    public QuoteCreatorMenuView provideQuoteCreatorMenuView(){
        return (QuoteCreatorMenuView) quotesCreatorViewInjector.getSupportFragmentManager().findFragmentById(R.id.quoteCreatorMenuFragment);
    }

    @Provides
    public QuoteListMenuView provideQuoteListMenuView(){
        return (QuoteListMenuView) quotesListViewInjector.getSupportFragmentManager().findFragmentById(R.id.quoteListMenuFragment);
    }

    @Provides
    public List<QuoteOptionComponents> provideQuoteOptionComponents() {
        return new ArrayList<QuoteOptionComponents>() {{
            add(new QuoteOptionComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteFontImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteFontExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteFontRv));
            add(new QuoteOptionComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteTextSizeRv));
            add(new QuoteOptionComponents(quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorImgBtn, quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorExpandingLayout, quoteCreatorMenuFragmentInjector.getBinding().quoteTextColorRv));
        }};
    }


}

