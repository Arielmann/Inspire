package inspire.ariel.inspire.common.singlequote;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.RelativeLayout;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import inspire.ariel.inspire.databinding.ActivitySingleQuoteBinding;

public class SingleQuoteActivity extends AppCompatActivity {

    ActivitySingleQuoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_quote);
        Quote quote = getIntent().getParcelableExtra(AppStrings.KEY_QUOTE);
        setQuoteDataInView(quote);
    }

    private void setQuoteDataInView(Quote quote){
        initQuoteImage(quote);
        binding.singleQuoteTv.setMovementMethod(new ScrollingMovementMethod());
        binding.singleQuoteTv.setText(quote.getText());
        binding.singleQuoteTv.setTextColor(quote.getTextColor());
        binding.singleQuoteTv.setTextSize(quote.getTextSize());
        FontsManager.getInstance().setFontOnTV(quote.getFontPath(), binding.singleQuoteTv);
    }

    private void initQuoteImage(Quote quote){
        Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + quote.getBgImageName()); //TODO: try catch for failures
        quote.setImage(ImageUtils.createDrawableFromUri(uri, getContentResolver(), getResources()));
        binding.singleQuoteLayout.setBackground(quote.getImage());
    }
}
