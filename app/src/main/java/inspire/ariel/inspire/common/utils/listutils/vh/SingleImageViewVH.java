package inspire.ariel.inspire.common.utils.listutils.vh;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import inspire.ariel.inspire.R;

public class SingleImageViewVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewVH.class.getSimpleName();

    public SingleImageViewVH(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(Bitmap bitmap, View.OnClickListener onImageClicked) {
        if(bitmap != null){
            ImageView imgView = itemView.findViewById(R.id.quoteOptionItemImageView);
            imgView.setImageBitmap(bitmap);
            imgView.setOnClickListener(onImageClicked);
        }
    }
}
