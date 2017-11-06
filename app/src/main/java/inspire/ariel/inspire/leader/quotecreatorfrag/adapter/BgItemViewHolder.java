package inspire.ariel.inspire.leader.quotecreatorfrag.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import inspire.ariel.inspire.R;

public class BgItemViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = BgItemViewHolder.class.getSimpleName();

    BgItemViewHolder(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(Bitmap image) {
        if(image != null){
            ImageButton imageButton = itemView.findViewById(R.id.bgItemImgButton);
            imageButton.setImageBitmap(image);
        }
    }
}
