package inspire.ariel.inspire.common.utils.listutils.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;

import java.util.List;

import inspire.ariel.inspire.common.utils.listutils.ListPresentable;
import inspire.ariel.inspire.common.utils.listutils.vh.GenericViewHolder;


class RecyclerViewWithHeaderFooterAdapter extends HeaderRecyclerViewAdapter<GenericViewHolder, ListPresentable, ListPresentable, GenericViewHolder> {

    private List<ListPresentable> dataSet;
    private GenericViewHolder headerView;
    private ViewHoldersFactory vhFactory;

    @Override
    protected GenericViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        //final View headerView = getLayoutInflater(parent).inflate(R.layout.header_course_details, parent, false);
        return headerView;
    }

    @Override
    public GenericViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        //final View view = getLayoutInflater(parent).inflate(R.layout.vh_quote, parent, false);
        return vhFactory.newViewHolder();
    }

    @Override
    public void onBindItemViewHolder(GenericViewHolder holder, int position) {
        holder.itemView.setOnClickListener((View.OnClickListener) holder);
        holder.setUIDataOnView(dataSet.get(position)); //-1 for header
    }

    @Override
    protected void onBindHeaderViewHolder(GenericViewHolder holder, int position) {
        holder.setUIDataOnView(dataSet.get(position));
    }

    private LayoutInflater getLayoutInflater(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    static class Builder {
        // Required parameters
        private List<ListPresentable> dataSet;
        private ViewHoldersFactory vhFactory;

        // Optional parameters - initialized to default values
        private Context context;
        private GenericViewHolder headerView;

        public Builder(ViewHoldersFactory vhFactory, List<ListPresentable> dataSet) {
            this.vhFactory = vhFactory;
            this.dataSet = dataSet;
        }

        public Builder withHeader(GenericViewHolder headerView) {
            this.headerView = headerView;
            return this;
        }

        public Builder withContext(Context context) {
            this.context = context;
            return this;
        }

        public RecyclerViewWithHeaderFooterAdapter build() {
            return new RecyclerViewWithHeaderFooterAdapter(this);
        }
    }
    private RecyclerViewWithHeaderFooterAdapter(Builder builder) {
        this.dataSet = builder.dataSet;
        this.headerView = builder.headerView;
        this.vhFactory = builder.vhFactory;
    }

}



