/*
package inspire.ariel.inspire.common.utils.listutils.vh;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class BoundaryViewHolder extends GenericViewHolder {

    private int boundaryHeight;
    private CompoBoundaryLineBinding binding;

    public BoundaryViewHolder(ViewGroup viewGroup, View itemView, int boundaryHeight) {
        super(itemView);
        binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.compo_boundary_line, viewGroup, false);
        this.boundaryHeight = boundaryHeight;
    }

    @Override
    public void setUIDataOnView(int position) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, boundaryHeight);
        FrameLayout layout = (FrameLayout) binding.boundaryLine.getParent();
        layout.setLayoutParams(params);
    }
}
*/
