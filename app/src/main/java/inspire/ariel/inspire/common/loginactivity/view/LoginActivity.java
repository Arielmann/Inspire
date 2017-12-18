package inspire.ariel.inspire.common.loginactivity.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerViewComponent;
import inspire.ariel.inspire.common.di.RecyclerViewsModule;
import inspire.ariel.inspire.common.di.PresentersModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.di.ViewInjector;
import inspire.ariel.inspire.common.di.ViewsModule;
import inspire.ariel.inspire.common.loginactivity.presenter.LoginPresenter;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements LoginView, ViewInjector{


    //4267B2 - Facebook blue color

    private ActivityLoginBinding binding;

    @Inject
    LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        inject();
        binding.fbBackendlessLoginBtn.setOnClickListener(view -> presenter.login());
    }

    private void inject() {
        DaggerViewComponent.builder()
                .appModule(new AppModule((InspireApplication) getApplication()))
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getApplication()).getAppComponent()).loginView(this).build())
                .viewsModule(ViewsModule.builder().viewsInjector(this).build())
                .recyclerViewsModule(new RecyclerViewsModule())
                .build()
                .inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.getFbCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUserLoggedIn() {
        ActivityStarter.startActivity(this, TreatsListActivity.class);
    }

    @Override
    public void onServerOperationFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
