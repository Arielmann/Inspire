
package inspire.ariel.inspire.common.drawer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.di.ViewsInjector;
import inspire.ariel.inspire.common.treatslist.view.TreatsListView;
import inspire.ariel.inspire.databinding.FragmentDrawerBinding;
import lombok.Getter;
import lombok.Setter;

public class DrawerFragment extends Fragment implements DrawerFragmentView, ViewsInjector {

    //TODO: make less choppy when going between screens

  /*  @Inject*/
    Drawer drawer;

   /* @Inject
    @Named(AppStrings.LOGOUT_DRAWER_ITEM)*/
    SecondaryDrawerItem logoutItem;

   /* @Inject
    @Named(AppStrings.PURCHASE_HISTORY_DRAWER_ITEM)*/
    SecondaryDrawerItem purchaseHistoryItem;

    @Getter
    private FragmentDrawerBinding drawerFragmentBinding;
    @Setter
    private TreatsListView treatsListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        drawerFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_drawer, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(drawerFragmentBinding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
        return drawerFragmentBinding.getRoot();
    }

    @Override
    public void init(TreatsListView treatListView) {
        this.treatsListView = treatListView;
        inject();
        configDrawer();
    }

    @Override
    public void addExtraDrawerItems(List<SecondaryDrawerItem> extraItems) {
        for (SecondaryDrawerItem item : extraItems) {
            drawer.addItems(item);
        }
    }

    private void inject() {
      /*  DaggerViewComponent.builder()
                .appModule(new AppModule((InspireApplication) getActivity().getApplication()))
                .resourcesModule(new ResourcesModule(getResources(), getActivity().getAssets()))
                .presentersModule(PresentersModule.builder().appComponent(((InspireApplication) getActivity().getApplication()).getAppComponent()).build())
                .viewsModule(ViewsModule.builder().viewsInjector(this).build())
                .recyclerViewsModule(new RecyclerViewsModule())
                .build()
                .inject(this);*/
    }

    private void configDrawer() {

        drawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withToolbar(drawerFragmentBinding.toolbar)
                .withActionBarDrawerToggle(true)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {drawer.openDrawer(); return true;})
                .addDrawerItems(purchaseHistoryItem, logoutItem)
                .buildForFragment();

        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(drawerFragmentBinding.toolbar);
        drawer.addItems(this.purchaseHistoryItem, this.logoutItem);

       /* this.purchaseHistoryItem.withOnDrawerItemClickListener((view, position, drawerItem) -> {
            drawer.closeDrawer();
            Intent intent = new Intent(treatsListView.getContext(), PurchasesHistoryFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        });*/

        logoutItem.withOnDrawerItemClickListener((view, position, drawerItem) -> {
            new MaterialDialog.Builder(treatsListView.getContext())
                    .title(R.string.really_logout_title)
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText(R.string.ok)
                    .onPositive((dialog, which) -> {
                        treatsListView.showProgressDialog(treatsListView.getLoginLogoutProgressDialog());
                        drawer.closeDrawer();
                        treatsListView.getPresenter().logout();
                    })
                    .negativeText(R.string.cancel)
                    .onNegative((dialog, which) -> dialog.cancel())
                    .cancelable(true)
                    .show();
            return true;
        });
    }
}

