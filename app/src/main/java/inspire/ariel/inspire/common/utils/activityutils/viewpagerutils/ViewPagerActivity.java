package inspire.ariel.inspire.common.utils.activityutils.viewpagerutils;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.databinding.ActivityAbstractViewPagerBinding;


public abstract class ViewPagerActivity extends AppCompatActivity {

    private ActivityAbstractViewPagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_abstract_view_pager);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.viewPagerTabs.setupWithViewPager(binding.viewPager);
    }
}
