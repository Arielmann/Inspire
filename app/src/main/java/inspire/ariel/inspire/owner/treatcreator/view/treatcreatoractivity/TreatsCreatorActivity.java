package inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity;

import android.app.Activity;
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
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.di.ViewInjector;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.listutils.SingleBitmapListAdapter;
import inspire.ariel.inspire.databinding.ActivityTreatCreatorBinding;
import inspire.ariel.inspire.owner.treatcreator.presenter.TreatsCreatorPresenter;
import inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment.TreatCreatorMenuView;
import lombok.Getter;

public class TreatsCreatorActivity extends AppCompatActivity implements TreatsCreatorViewController, TreatsCreatorViewTreatProps, ViewInjector {

    @Inject
    Handler backgroundChangeHandler;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.TREAT_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject
    int treatVhLayoutInt;

    @Inject
    @Getter
    TreatsCreatorPresenter presenter;

    @Inject
    @Named(AppStrings.MAIN_PROGRESS_DIALOG)
    KProgressHUD progressDialog;

    @Inject
    List<View> disappearingViews;

    @Inject
    TreatCreatorMenuView treatCreatorMenuView;

    private String TAG = TreatsCreatorActivity.class.getName();
    @Getter private ActivityTreatCreatorBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_treat_creator);
        inject();
        binding.postImageBtn.setOnClickListener(view -> requestTreatPost());
        setTreatFont(FontsManager.Font.ALEF_BOLD);
        initBgPicker();
        initKeyboardChangeBehaviours();
        initTreatMenuView();
        Log.d(TAG, TAG + " onCreate() method completed");
    }


    private void initTreatMenuView() {
        treatCreatorMenuView.setTreatsCreatorActivityViewProps(this);
        treatCreatorMenuView.willInject(((InspireApplication) getApplication()).getAppComponent(), this, getAssets());
        treatCreatorMenuView.initView();
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule(((InspireApplication) getApplication())))
                .listsModule(new ListsModule())
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).treatsCreatorViewController(this).build())
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

        if (treatCreatorMenuView.areAllOptionLayoutsCollapsed()) {
            super.onBackPressed();
        } else {
            treatCreatorMenuView.collapseAllOptionLayouts();
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
        DiscreteScrollView bgPicker = binding.bgPicker;
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

    void requestTreatPost() {
        Treat newTreat = createTreatForPost();
        presenter.requestPostTreat(newTreat);
    }

    Treat createTreatForPost() {
        final String text = binding.treatEditText.getText().toString();
        final int textColor = binding.treatEditText.getCurrentTextColor();
        final int textSize = Math.round(binding.treatEditText.getTextSize() * Percentages.FIFTY_FIVE);
        return Treat.builder().text(text)
                .fontPath(presenter.getFontPath())
                .textColor(textColor)
                .textSize(textSize)
                .ownerId(AppStrings.VAL_OWNER_OBJECT_ID)
                .bgImageName(presenter.getBgImgName())
                .build();
    }

    @Override
    public void setBackground(Drawable background) {
        binding.treatEditText.setBackground(background);
    }

    /**
     * Note: Never call from TreatCreatorPresenter, will create StackOverFlow due to recursive method call
     */
    @Override
    public void refreshCurrentBackground() {
        setBackground(presenter.getChosenBgImage()); //Required to prevent keyboard show/hide unexpected bugs
    }

    @Override
    public void setTreatTextSize(int size) {
        binding.treatEditText.setTextSize(size);
    }

    @Override
    public void setTreatFont(FontsManager.Font font) {
        FontsManager.getInstance().setFontOnTV(font, binding.treatEditText);
    }

    @Override
    public void setTreatTextColor(int color) {
        binding.treatEditText.setHintTextColor(color);
        binding.treatEditText.setTextColor(color);
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
                .onPositive((dialog, which) -> goToOtherActivity(new Intent(TreatsCreatorActivity.this, TreatsListActivity.class)))
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
        setResult(Activity.RESULT_OK, intent);
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
