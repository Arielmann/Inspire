package inspire.ariel.inspire.common.utils.listutils.vh;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import inspire.ariel.inspire.R;

public class SingleImageViewButtonVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewButtonVH.class.getSimpleName();

    public SingleImageViewButtonVH(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(Bitmap image) {
        if(image != null){
            ImageButton imgBtn = itemView.findViewById(R.id.quoteOptionImgBtn);
            imgBtn.setImageBitmap(image);
        }
    }
}
