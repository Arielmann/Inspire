package inspire.ariel.inspire.common.treatslist.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewInjector;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapter;
import inspire.ariel.inspire.common.treatslist.events.OnTreatDeleteClickedEvent;
import inspire.ariel.inspire.common.treatslist.events.OnTreatUpdatedEvent;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenter;
import inspire.ariel.inspire.common.treatslist.view.optionsmenufragment.TreatListMenuView;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import inspire.ariel.inspire.databinding.ActivityTreatsListBinding;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatEditorActivity;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorActivity;
import lombok.Getter;

public class TreatsListActivity extends AppCompatActivity implements TreatsListView, ViewInjector {

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

    @Inject //Views Module
    TreatListMenuView treatListMenuView;

    @Getter private static boolean isInStack;
    private final String TAG = TreatsListActivity.class.getName();
    private final Set<KProgressHUD> activeProgressDialogs = new HashSet<>();
    private ActivityTreatsListBinding binding;

    /**
     * Initialization
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInStack = true;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_treats_list);
        inject();
        showProgressDialog(mainProgressDialog);
        ((InspireApplication) getApplication()).initApp(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                initActivity();
            }

            @Override
            public void onFailure(String errorForUser) {
                //TODO: if local db support available, load it instead of presenting error
                dismissProgressDialog(mainProgressDialog);
                showToastErrorMessage(errorForUser);
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
                .listsModule(new ListsModule())
                .build()
                .inject(this);
    }

    private void initActivity() {
        treatListMenuView.init(this);
        //TODO: You can't know between treats download or login check - who will finish first. Therefore, dismiss progress dialog only after both finish
        presenter.init(adapter);
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

    /**
     * Optional Life Cycle Methods
     */

    @Override
    protected void onStart() {
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
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        isInStack = false;
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppNumbers.TREAT_EDIT_RESULT_OK) {
            presenter.onTreatUpdated(data);
            return;
        }
        presenter.getFbCallbackManager().onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"Is user logged in via focaebook? " + AccessToken.getCurrentAccessToken());
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Event bus
     */

    @Subscribe
    public void onTreatDeleteClickedEvent(OnTreatDeleteClickedEvent event) {
        showReallyDeleteDialog(event.getPosition());
    }

    @Subscribe
    public void onTreatUpdateClickedEvent(OnTreatUpdatedEvent event) {
        Intent intent = new Intent(binding.getRoot().getContext(), TreatEditorActivity.class);
        intent.putExtra(AppStrings.KEY_TREAT, event.getTreat());
        intent.putExtra(AppStrings.KEY_TREAT_POSITION, event.getPosition());
        ((TreatsListActivity) binding.getRoot().getContext()).startActivityForResult(intent, AppNumbers.DEFAULT_REQUEST_CODE);
    }

    /**
     * User login status adaptation
     */

    @Override
    public void onUserLoggedIn() {
        dismissProgressDialog(loginLogoutProgressDialog);
        binding.goToCreateTreatActivityBtn.setVisibility(View.VISIBLE);
        treatListMenuView.resetLoginLogoutBtn(getResources().getDrawable(R.drawable.logout_icon), treatListMenuView.getOnLogoutClicked());
        binding.goToCreateTreatActivityBtn.setOnClickListener(view -> ActivityStarter.startActivity(TreatsListActivity.this, TreatsCreatorActivity.class));
    }

    @Override
    public void onUserLoggedOut() {
        dismissProgressDialog(loginLogoutProgressDialog);
        binding.goToCreateTreatActivityBtn.setVisibility(View.GONE);
        treatListMenuView.resetLoginLogoutBtn(getResources().getDrawable(R.drawable.login_icon), treatListMenuView.getOnLoginClicked());
        binding.goToCreateTreatActivityBtn.setOnClickListener(view -> Toast.makeText(view.getContext(), getResources().getString(R.string.error_please_login), Toast.LENGTH_SHORT).show());
    }

    /**
     * Progress Dialog
     */

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

    /**
     * Show Messages
     */

    @Override
    public void showToastErrorMessage(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServerOperationFailed(String error) {
        dismissAllProgressDialogs();
        showToastErrorMessage(error);
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
                    presenter.deleteTreat(treatPosition);
                })
                .negativeText(R.string.cancel)
                .onNegative((dialog, which) -> dialog.cancel())
                .cancelable(true)
                .show();
    }

    /**
     * Getters
     */

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * DiscreteScrollView Programmatic Scrolling
     */

    @Override
    public void scrollTreatListToTop() {
        binding.treatsRv.scrollToPosition(0);
    }
}



