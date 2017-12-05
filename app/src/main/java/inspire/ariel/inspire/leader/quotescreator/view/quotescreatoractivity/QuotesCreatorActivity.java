package inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.animationutils.AnimatedSlidingView;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.listutils.SingleBitmapListAdapter;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment.QuoteCreatorMenuView;
import lombok.Getter;

public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorViewController, QuotesCreatorViewQuoteProperties, QuotesCreatorActivityInjector {

    @Inject
    Handler backgroundChangeHandler;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.QUOTE_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject
    int quoteVhLayoutInt;

    @Inject
    QuotesCreatorPresenter presenter;

    @Inject
    @Named(AppStrings.QUOTES_CREATOR_PROGRESS_DIALOG)
    KProgressHUD progressHUD;

    @Inject
    List<AnimatedSlidingView> disappearingViews;

    @Inject
    QuoteCreatorMenuView quoteCreatorMenuView;

    private String TAG = QuotesCreatorActivity.class.getName();
    @Getter private ActivityQuoteCreatorBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        inject();
        binding.postImageView.setOnClickListener(onPostQuoteClicked);
        setQuoteFont(FontsManager.Font.ALEF_BOLD);
        initBgPicker();
        initKeyboardChangeBehaviours();
        initQuoteOptionsView();
    }

    private void initQuoteOptionsView() {
        quoteCreatorMenuView.setQuotesCreatorActivityViewProperties(this);
        quoteCreatorMenuView.willInject(((InspireApplication) getApplication()).getAppComponent(), this, getAssets());
        quoteCreatorMenuView.initView();
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule(((InspireApplication) getApplication())))
                .listsModule(new ListsModule())
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).quotesCreatorViewController(this).build())
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .viewsModule(ViewsModule.builder().quotesCreatorViewInjector(this).build())
                .build()
                .inject(this);
    }

    /**
     * Required to prevent background changes bug
     */
    private void setBackgroundWithDelay(long delay) {
        backgroundChangeHandler.postDelayed(() -> {
            setBackground(presenter.getChosenBgImage());
        }, delay);
    }

    @Override
    public void onBackPressed() {

        if (quoteCreatorMenuView.areAllOptionLayoutsCollapsed()) {
            super.onBackPressed();
        } else {
            quoteCreatorMenuView.collapseAllOptionLayouts();
        }
    }

    @Override
    protected void onStart() {
        setBackgroundWithDelay(AppTimeMillis.QUARTER_SECOND); //Quick call for a clean user experience
        setBackgroundWithDelay(AppTimeMillis.HALF_SECOND); //Slow call for eliminating bug's edge cases
        super.onStart();
    }

    @Override
    protected void onStop() {
        backgroundChangeHandler.removeCallbacksAndMessages(null); //Prevent memory leaks
        super.onStop();
    }

    private void initBgPicker() {
        SingleBitmapListAdapter adapter = new SingleBitmapListAdapter(customResourcesProvider.getBackgroundImages(), quoteVhLayoutInt);
        DiscreteScrollView bgPicker = binding.bgPicker;
        bgPicker.setSlideOnFling(discreteScrollViewData.isSetSlideOnFling());
        bgPicker.setAdapter(adapter);
        bgPicker.addOnItemChangedListener(presenter.getOnItemChangedListener());
        bgPicker.addScrollStateChangeListener(presenter.getOnScrollStateChangedListener());
        bgPicker.setItemTransitionTimeMillis(discreteScrollViewData.getTimeMillis());
        bgPicker.setOffscreenItems(discreteScrollViewData.getOffScreenItems());
        bgPicker.setItemTransformer(discreteScrollViewData.getScaleTransformer());
    }

    private void initKeyboardChangeBehaviours() {
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                for (AnimatedSlidingView disappearingView : disappearingViews) {
                    disappearingView.getView().animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND);
                    disappearingView.getView().animate().translationY(disappearingView.getEndAnimatedYPos());
                }
                binding.postImageView.setClickable(false);
                setBackground(presenter.getChosenBgImage()); //Required to prevent background changes bug
            } else {
                for (AnimatedSlidingView disappearingView : disappearingViews) {
                    disappearingView.getView().animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
                    ;
                    disappearingView.getView().animate().translationY(disappearingView.getInitialYPos());
                }
                binding.postImageView.setClickable(true);
                setBackground(presenter.getChosenBgImage()); //Required to prevent background changes bug
            }
        });
    }

    View.OnClickListener onPostQuoteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String text = binding.quoteEditText.getText().toString();
            final int textColor = binding.quoteEditText.getCurrentTextColor();
            final int textSize = Math.round(binding.quoteEditText.getTextSize() * Percentages.FIFTY_FIVE);
            boolean canUploadQuote = presenter.validateQuote(text);

            if (canUploadQuote) {
                Quote quote = Quote.builder().text(text)
                        .fontPath(presenter.getFontPath())
                        .textColor(textColor)
                        .textSize(textSize)
                        .leaderId(AppStrings.VAL_LEADER_OBJECT_ID)
                        .bgImageName(presenter.getBgImgName())
                        .build();
                presenter.postQuote(quote);
            }
        }
    };

    @Override
    public void setBackground(Drawable background) {
        binding.quoteEditText.setBackground(background);
    }

    /**
     * Note: Never call from QuoteCreatorPresenter, will create StackOverFlow due to recursive method call
     */
    @Override
    public void refreshCurrentBackground() {
        setBackground(presenter.getChosenBgImage()); //Required to prevent keyboard show/hide unexpected bugs
    }

    @Override
    public void setQuoteTextSize(int size) {
        binding.quoteEditText.setTextSize(size);
    }

    @Override
    public void setQuoteFont(FontsManager.Font font) {
        FontsManager.getInstance().setFontOnTV(font, binding.quoteEditText);
    }

    @Override
    public void setQuoteTextColor(int color) {
        binding.quoteEditText.setHintTextColor(color);
        binding.quoteEditText.setTextColor(color);
    }

    @Override
    public void dismissProgressDialogAndShowErrorMessage(String message) {
        dismissProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goToOtherActivity(Class newActivity, Intent intent) {
        startActivity(intent);
        presenter.onDestroy();
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
    }

    //TODO: Allow cancellation
    @Override
    public void showProgressDialog() {
        progressHUD.show();
    }
}
