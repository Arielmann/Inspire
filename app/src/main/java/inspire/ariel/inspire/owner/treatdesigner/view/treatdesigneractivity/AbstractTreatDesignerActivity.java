package inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.RecyclerViewsModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsInjector;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.listutils.SingleBitmapListAdapter;
import inspire.ariel.inspire.databinding.ActivityAbstractTreatDesignerBinding;
import inspire.ariel.inspire.owner.treatdesigner.presenter.TreatsDesignerPresenter;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuView;
import lombok.Getter;

public abstract class AbstractTreatDesignerActivity extends AppCompatActivity implements TreatsDesignerViewController, TreatsDesignerViewTreatProps, ViewsInjector {

    @Inject
    Handler backgroundChangeHandler;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.TREAT_DESIGNER_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject
    int treatVhLayoutInt;

    @Inject
    @Getter
    TreatsDesignerPresenter presenter;

    @Inject
    @Named(AppStrings.MAIN_PROGRESS_DIALOG)
    KProgressHUD progressDialog;

    @Inject
    List<View> disappearingViews;

    @Inject
    TreatDesignerMenuView treatDesignerMenuView;

    private String TAG = AbstractTreatDesignerActivity.class.getName();
    @Getter private ActivityAbstractTreatDesignerBinding abstractTreatDesignerBinding;

    /**
     * Abstractions
     */
    abstract void requestTreatPost();

    /**
     * Required lifecycle methods
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        abstractTreatDesignerBinding = DataBindingUtil.setContentView(this, R.layout.activity_abstract_treat_designer);
        inject();
        abstractTreatDesignerBinding.postImageBtn.setOnClickListener(view -> requestTreatPost());
        setTreatFont(FontsManager.Font.ALEF_BOLD);
        initBgPicker();
        initKeyboardChangeBehaviours();
        initTreatMenuView();
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    private void initTreatMenuView() {
        treatDesignerMenuView.setTreatsDesignerActivityViewProps(this);
        treatDesignerMenuView.willInject(((InspireApplication) getApplication()).getAppComponent(), this, getAssets());
        treatDesignerMenuView.init();
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule(((InspireApplication) getApplication())))
                .recyclerViewsModule(new RecyclerViewsModule())
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).treatsDesignerViewController(this).build())
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .viewsModule(ViewsModule.builder().viewsInjector(this).build())
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

        if (treatDesignerMenuView.areAllOptionLayoutsCollapsed()) {
            super.onBackPressed();
        } else {
            treatDesignerMenuView.collapseAllOptionLayouts();
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
        SingleBitmapListAdapter adapter = new SingleBitmapListAdapter(customResourcesProvider.getBackgroundImages(), treatVhLayoutInt);
        DiscreteScrollView bgPicker = abstractTreatDesignerBinding.bgPicker;
        bgPicker.setSlideOnFling(discreteScrollViewData.isSetSlideOnFling());
        bgPicker.setAdapter(adapter);
        bgPicker.addOnItemChangedListener(presenter.getOnItemChangedListener());
        bgPicker.setItemTransitionTimeMillis(discreteScrollViewData.getTimeMillis());
        bgPicker.setOffscreenItems(discreteScrollViewData.getOffScreenItems());
        bgPicker.setItemTransformer(discreteScrollViewData.getScaleTransformer());
    }

    private void initKeyboardChangeBehaviours() {
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                hideTypingRedundantViews();
                setBackground(presenter.getChosenBgImage()); //Required to prevent background changes bug
            } else {
                showTypingRedundantViews();
                setBackground(presenter.getChosenBgImage()); //Required to prevent background changes bug
            }
        });
    }

    private void showTypingRedundantViews() {
        for (View disappearingView : disappearingViews) {
            disappearingView.setVisibility(View.VISIBLE);
            disappearingView.animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
            disappearingView.setClickable(true);
        }
    }

    private void hideTypingRedundantViews() {
        for (View disappearingView : disappearingViews) {
            disappearingView.setClickable(false);
            disappearingView.animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND).withEndAction((() -> disappearingView.setVisibility(View.GONE)));
        }
    }

    Treat createTreatForPost() {
        final String text = abstractTreatDesignerBinding.treatEditText.getText().toString();
        final int textColor = abstractTreatDesignerBinding.treatEditText.getCurrentTextColor();
        final int textSize = Math.round(abstractTreatDesignerBinding.treatEditText.getTextSize() * Percentages.FIFTY_FIVE);
        return Treat.builder().text(text)
                .fontPath(presenter.getFontPath())
                .textColor(textColor)
                .textSize(textSize)
                .ownerId(AppStrings.BACKENDLESS_VAL_OWNER_ID)
                .bgImageName(presenter.getBgImgName())
                .build();
    }

    @Override
    public void setBackground(Drawable background) {
        abstractTreatDesignerBinding.treatEditText.setBackground(background);
    }

    /**
     * Note: Never call from TreatDesignerPresenter, will create StackOverFlow due to recursive method call
     */
    @Override
    public void refreshCurrentBackground() {
        setBackground(presenter.getChosenBgImage()); //Required to prevent keyboard show/hide unexpected bugs
    }

    @Override
    public void setTreatTextSize(int size) {
        abstractTreatDesignerBinding.treatEditText.setTextSize(size);
    }

    @Override
    public void setTreatFont(FontsManager.Font font) {
        FontsManager.getInstance().setFontOnTV(font, abstractTreatDesignerBinding.treatEditText);
    }

    @Override
    public void setTreatTextColor(int color) {
        abstractTreatDesignerBinding.treatEditText.setHintTextColor(color);
        abstractTreatDesignerBinding.treatEditText.setTextColor(color);
    }

    @Override
    public void dismissProgressDialogAndShowErrorMessage(String message) {
        dismissProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showErrorDialogAndGoBackToTreatListActivity() {
        new MaterialDialog.Builder(this)
                .title(R.string.error_title)
                .titleGravity(GravityEnum.CENTER)
                .content(getResources().getString(R.string.error_invalid_user_permissions))
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    AbstractTreatDesignerActivity.this.dismissProgressDialog();
                    goToOtherActivity(new Intent(AbstractTreatDesignerActivity.this, TreatsListActivity.class));
                })
                .show();
    }

    @Override
    public void goToOtherActivity(Intent otherActivityData) {
        startActivity(otherActivityData);
        presenter.onDestroy();
        finish();
    }

    @Override
    public void sendResultToActivity(Intent intent) {
        setResult(AppNumbers.TREAT_EDIT_RESULT_OK, intent);
        presenter.onDestroy();
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //TODO: Allow cancellation
    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }
}
