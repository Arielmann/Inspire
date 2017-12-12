package inspire.ariel.inspire.common.utils.listutils;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import inspire.ariel.inspire.R;

class SingleImageViewVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewVH.class.getSimpleName();
    private ImageView imgView;

    public SingleImageViewVH(View itemView) {
        super(itemView);
        imgView = itemView.findViewById(R.id.treatOptionItemImageView);
    }

    public void setUIDataOnView(Drawable image) {
        if (image != null) {
            imgView.setImageDrawable(image);
            Log.i(TAG, "imageView set for bitmap " + image);
        }
    }
}
