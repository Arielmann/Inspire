package inspire.ariel.inspire.common.utils.listutils.vh;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import inspire.ariel.inspire.R;

public class SingleTvVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewButtonVH.class.getSimpleName();

    public SingleTvVH(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(String text) {
            TextView tv = itemView.findViewById(R.id.singleTv);
            tv.setText(text);
    }
}
