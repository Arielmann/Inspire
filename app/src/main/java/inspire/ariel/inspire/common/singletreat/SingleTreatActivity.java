package inspire.ariel.inspire.common.singletreat;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import inspire.ariel.inspire.databinding.ActivitySingleTreatBinding;

public class SingleTreatActivity extends AppCompatActivity {

    ActivitySingleTreatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_treat);
        Treat treat = getIntent().getParcelableExtra(AppStrings.KEY_TREAT);
        setTreatDataInView(treat);
    }

    private void setTreatDataInView(Treat treat){
        initTreatImage(treat);
        binding.singleTreatTv.setMovementMethod(new ScrollingMovementMethod());
        binding.singleTreatTv.setText(treat.getText());
        binding.singleTreatTv.setTextColor(treat.getTextColor());
        binding.singleTreatTv.setTextSize(treat.getTextSize());
        FontsManager.getInstance().setFontOnTV(treat.getFontPath(), binding.singleTreatTv);
    }

    private void initTreatImage(Treat treat){
        Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + treat.getBgImageName()); //TODO: try catch for failures
        treat.setImage(ImageUtils.createDrawableFromUri(uri, getContentResolver(), getResources()));
        binding.singleTreatLayout.setBackground(treat.getImage());
    }
}
