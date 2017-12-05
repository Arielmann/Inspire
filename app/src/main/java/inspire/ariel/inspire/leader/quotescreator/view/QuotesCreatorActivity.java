package inspire.ariel.inspire.leader.quotescreator.view;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.listutils.SingleBitmapListAdapter;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenterImpl;

public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorView, QuotesCreatorViewForFragments {

    @Inject
    Handler backgroundChangeHandler;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.QUOTE_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject
    int quoteVhLayoutNumber;

    private String TAG = QuotesCreatorActivity.class.getName();
    private QuotesCreatorPresenter presenter;
    private KProgressHUD progressHUD;
    private ActivityQuoteCreatorBinding binding;
    private List<AnimatedSlidingView> disappearingViews;
    private QuoteCreatorMenuView quoteOptionsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        binding.postImageView.setOnClickListener(onPostQuoteClicked);
        ((InspireApplication) getApplication()).getAppComponent().inject(this);
        setQuoteFont(FontsManager.Font.ALEF_BOLD);
        presenter = new QuotesCreatorPresenterImpl(((InspireApplication) getApplication()).getAppComponent(), this);
        initBgPicker();
        initKeyboardChangeBehaviours();
        quoteOptionsView = (QuoteCreatorMenuView) getSupportFragmentManager().findFragmentById(R.id.quoteOptionsFrag);
        quoteOptionsView.setQuotesCreatorActivityView(this);
    }

    /**Required to prevent background changes bug*/
    private void setBackgroundWithDelay(long delay) {
        backgroundChangeHandler.postDelayed(() -> {
            setBackground(presenter.getChosenBgImage());
        }, delay);
    }

    @Override
    public void onBackPressed() {

        if(quoteOptionsView.areAllOptionLayoutsCollapsed()){
            super.onBackPressed();
        } else{
            quoteOptionsView.collapseAllOptionLayouts();
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

    private void initAgPickerAnimationListeners() {
        AnimatedSlidingView slidingBgPicker = AnimatedSlidingView.builder()
                .view(binding.bgPicker)
                .initialYPos(binding.bgPicker.getTranslationY())
                .endAnimatedYPos(binding.bgPicker.getTranslationY() * Percentages.FIVE_HUNDRED)
                .build();

        AnimatedSlidingView slidingPostImageView = (AnimatedSlidingView.builder()
                .view(binding.postImageView)
                .initialYPos(binding.postImageView.getTranslationY())
                .endAnimatedYPos(binding.postImageView.getTranslationY() * Percentages.FIVE_HUNDRED)
                .build());

        disappearingViews = new ArrayList<AnimatedSlidingView>() {{
            add(slidingBgPicker);
            add(slidingPostImageView);
        }};
    }

    private void initBgPicker() {
        SingleBitmapListAdapter adapter = new SingleBitmapListAdapter(customResourcesProvider.getBackgroundImages(), quoteVhLayoutNumber);
        DiscreteScrollView bgPicker = binding.bgPicker;
        bgPicker.setSlideOnFling(discreteScrollViewData.isSetSlideOnFling());
        bgPicker.setAdapter(adapter);
        bgPicker.addOnItemChangedListener(presenter.getOnItemChangedListener());
        bgPicker.addScrollStateChangeListener(presenter.getOnScrollStateChangedListener());
        bgPicker.setItemTransitionTimeMillis(discreteScrollViewData.getTimeMillis());
        bgPicker.setOffscreenItems(discreteScrollViewData.getOffScreenItems());
        bgPicker.setItemTransformer(discreteScrollViewData.getScaleTransformer());

        initAgPickerAnimationListeners();
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
                    disappearingView.getView().animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);;
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

    /**Note: Never call from QuoteCreatorPresenter, will create StackOverFlow due to recursive method call*/
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
    public void dismissProgressDialogAndShowUploadErrorMessage(String message) {
        dismissProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goToQuoteListActivity(Quote newQuote) {
        Intent intent = new Intent(this, QuotesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppStrings.KEY_QUOTE, newQuote);
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
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getString(R.string.please_wait))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
}
