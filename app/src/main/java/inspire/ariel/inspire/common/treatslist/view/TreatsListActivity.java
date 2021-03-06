package inspire.ariel.inspire.common.treatslist.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import hugo.weaving.DebugLog;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.RecyclerViewsModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewsInjector;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.loginactivity.view.LoginActivity;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapter;
import inspire.ariel.inspire.common.treatslist.events.OnTreatDeleteClickedEvent;
import inspire.ariel.inspire.common.treatslist.events.OnTreatsUpdateClickEvent;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenter;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import inspire.ariel.inspire.databinding.ActivityTreatsListBinding;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatEditorActivity;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsCreatorActivity;
import lombok.Getter;

@DebugLog
public class TreatsListActivity extends AppCompatActivity implements TreatsListView, ViewsInjector {

    @Inject //List Module
            TreatListAdapter adapter;

    @Inject //Views Module
    @Named(AppStrings.TREAT_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData discreteScrollViewData;

    @Inject //Views Module
    @Named(AppStrings.MAIN_PROGRESS_DIALOG)
    @Getter
    KProgressHUD mainProgressDialog;

    @Inject //Views Module
    @Named(AppStrings.PAGING_TREATS_LIST_PROGRESS_DIALOG)
    @Getter
    KProgressHUD pagingProgressDialog;

    @Inject //Views Module
    @Named(AppStrings.LOGIN_LOGOUT_PROGRESS_DIALOG)
    @Getter
    KProgressHUD loginLogoutProgressDialog;

    @Inject //Presenters Module
    @Getter
    TreatsListPresenter presenter;

    @Getter
    private static boolean isInForeground;
    @Getter
    private static boolean isInStack;
    private final String TAG = TreatsListActivity.class.getName();
    private final Set<KProgressHUD> activeProgressDialogs = new HashSet<>();
    private ActivityTreatsListBinding binding;

    //==============================================================================================
    //  Init
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInStack = true;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_treats_list);
        inject();
        presenter.prepareForFirstLaunchIfNeeded();
        ((InspireApplication) getApplication()).initAppForFirstTimeIfNeeded(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                initActivity();
            }

            @Override
            public void onFailure(String errorForUser) {
                dismissProgressDialog(mainProgressDialog);
                showSnackbarMessage(errorForUser);
                binding.goToCreateTreatActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_login), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule((InspireApplication) getApplication()))
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).treatsListView(this).build())
                .viewsModule(ViewsModule.builder().viewsInjector(this).build())
                .recyclerViewsModule(new RecyclerViewsModule())
                .build()
                .inject(this);
    }

    private void initActivity() {
        //TODO: You can't know between treats download or login check - who will finish first. Therefore, dismiss progress dialog only after both finish
        presenter.startOperations(adapter); //presenter's model already available so no crush
        initTreatsRecyclerView();
        binding.goToCreateTreatActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(TreatsListActivity.this, TreatsCreatorActivity.class));
        Log.d(TAG, TAG + " onCreate() method completed");
    }

    private void initTreatsRecyclerView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, OrientationHelper.VERTICAL);
        binding.treatsRv.addItemDecoration(dividerItemDecoration);
        binding.treatsRv.setHasFixedSize(discreteScrollViewData.isHasFixedSize());
        binding.treatsRv.setSlideOnFling(discreteScrollViewData.isSetSlideOnFling());
        binding.treatsRv.setAdapter(adapter);
        binding.treatsRv.addScrollStateChangeListener(presenter.getOnScrollChangedListener());
        binding.treatsRv.setOrientation(discreteScrollViewData.getOrientation());
        binding.treatsRv.setItemTransitionTimeMillis(discreteScrollViewData.getTimeMillis());
        binding.treatsRv.setOffscreenItems(discreteScrollViewData.getOffScreenItems());
        binding.treatsRv.setItemTransformer(discreteScrollViewData.getScaleTransformer());
    }

    //==============================================================================================
    //  Lifecycle methods
    //==============================================================================================

    @Override
    protected void onStart() {
        isInForeground = true;
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        presenter.OnNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        isInForeground = false;
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        isInStack = false;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppNumbers.TREAT_EDIT_RESULT_OK) {
            presenter.onTreatUpdated(data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //==============================================================================================
    //  Event bus
    //==============================================================================================

    @Subscribe
    public void onTreatDeleteClickedEvent(OnTreatDeleteClickedEvent event) {
        showReallyDeleteDialog(event.getPosition());
    }

    @Subscribe
    public void onTreatUpdateClickedEvent(OnTreatsUpdateClickEvent event) {
        Intent intent = new Intent(binding.getRoot().getContext(), TreatEditorActivity.class);
        intent.putExtra(AppStrings.KEY_TREAT, event.getTreat());
        intent.putExtra(AppStrings.KEY_TREAT_POSITION, event.getPosition());
        ((TreatsListActivity) binding.getRoot().getContext()).startActivityForResult(intent, AppNumbers.DEFAULT_REQUEST_CODE);
    }

    //==============================================================================================
    //  User login status adaptation
    //==============================================================================================

    @Override
    public void onUserLoggedIn() {
        dismissProgressDialog(loginLogoutProgressDialog);
        binding.goToCreateTreatActivityBtn.setVisibility(DataManager.getInstance().getUserStatusData().getGoToTreatDesignerVisibility());
        binding.goToCreateTreatActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(TreatsListActivity.this, TreatsCreatorActivity.class));
    }

    @Override
    public void onUserLoggedOut() {
        dismissProgressDialog(loginLogoutProgressDialog);
        ActivityStarter.startActivity(this, LoginActivity.class);
        binding.goToCreateTreatActivityBtn.setVisibility(View.GONE);
    }

    //==============================================================================================
    //  Progress dialog
    //==============================================================================================

    @Override
    public void showProgressDialog(KProgressHUD dialog) {
        if (dialog != null) {
            activeProgressDialogs.add(dialog);
            dialog.show();
        }
    }

    @Override
    public void dismissProgressDialog(KProgressHUD dialog) {
        if (dialog != null) {
            activeProgressDialogs.remove(dialog);
            dialog.dismiss();
        }
    }

    private void dismissAllProgressDialogs() {
        for (KProgressHUD dialog : activeProgressDialogs) {
            dismissProgressDialog(dialog);
        }
    }

    //==============================================================================================
    //  Show messages
    //==============================================================================================


    public void showSnackbarMessage(String message) {
        if (message.isEmpty()) {
            return;
        }
        Snackbar snackBar = Snackbar.make(binding.treatListLayout, message, AppTimeMillis.ALMOST_FOREVER);
        View errorView = snackBar.getView();
        TextView errorTv = errorView.findViewById(android.support.design.R.id.snackbar_text);
        errorTv.setMaxLines(AppNumbers.SNACK_BAR_MAX_LINES);
        snackBar.setAction(R.string.ok, view -> snackBar.dismiss());
        snackBar.show();
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onServerOperationFailed(String error) {
        dismissAllProgressDialogs();
        showSnackbarMessage(error);
    }

    @Override
    public void showReallyDeleteDialog(int treatPosition) {
        new MaterialDialog.Builder(this)
                .title(R.string.are_you_sure_title)
                .titleGravity(GravityEnum.CENTER)
                .content(getResources().getString(R.string.really_delete_msg))
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.delete_title)
                .onPositive((dialog, which) -> {
                    showProgressDialog(mainProgressDialog);
                    presenter.setTreatUnPurchaseable(treatPosition);
                })
                .negativeText(R.string.cancel)
                .onNegative((dialog, which) -> dialog.cancel())
                .cancelable(true)
                .show();
    }

    @Override
    public void showEnterAdminPasswordDialog(Treat treat, int treatPosition) {
        new MaterialDialog.Builder(this)
                .title(R.string.title_put_admin_password)
                .titleGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(AppStrings.EMPTY_STRING, AppStrings.EMPTY_STRING, (dialog, input) -> {
                    showProgressDialog(mainProgressDialog);
                    presenter.purchaseTreat(input.toString(), treat, treatPosition);
                })
                .negativeText(R.string.cancel)
                .onNegative((dialog, which) -> dialog.cancel())
                .cancelable(true)
                .show();
    }

    //==============================================================================================
    //  Getters
    //==============================================================================================

    @Override
    public Context getContext() {
        return this;
    }

    //==============================================================================================
    //  DiscreteScrollView
    //==============================================================================================

    /**
     * DiscreteScrollView programmatic scrolling
     */

    @Override
    public void scrollTreatListToTop() {
        binding.treatsRv.scrollToPosition(0);
    }
}



