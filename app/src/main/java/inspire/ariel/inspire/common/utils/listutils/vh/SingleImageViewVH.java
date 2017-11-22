package inspire.ariel.inspire.common.utils.listutils.vh;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;

public class SingleImageViewVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewVH.class.getSimpleName();

    public SingleImageViewVH(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(InspireBackgroundImage image, View.OnClickListener onImageClicked) {
        if(image.getDrawable() != null){
            ImageView imgView = itemView.findViewById(R.id.quoteOptionItemImageView);
            imgView.setImageDrawable(image.getDrawable());
            imgView.setOnClickListener(onImageClicked);
            Log.i(TAG, "imageView set for bitmap " + image.getName());
        }
    }
}
