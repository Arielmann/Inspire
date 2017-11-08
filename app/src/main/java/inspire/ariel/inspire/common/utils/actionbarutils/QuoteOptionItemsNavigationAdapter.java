package inspire.ariel.inspire.common.utils.actionbarutils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.actionbarutils.model.SpinnerNavItem;

public class QuoteOptionItemsNavigationAdapter extends BaseAdapter {

    private ImageView imageView;
    private TextView textView;
    private ArrayList<SpinnerNavItem> spinnerNavItem;
    private Context context;

    public QuoteOptionItemsNavigationAdapter(Context context, ArrayList<SpinnerNavItem> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }

    @Override
    public Object getItem(int index) {
        return spinnerNavItem.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.compo_quote_option_view, null);
        }

        imageView = convertView.findViewById(R.id.quoteOptionItemImageView);
        textView = convertView.findViewById(R.id.quoteOptionItemTv);
        imageView.setImageResource(spinnerNavItem.get(position).getIcon());
        imageView.setVisibility(View.GONE);
        textView.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.compo_quote_option_view, null);
        }

        imageView = convertView.findViewById(R.id.quoteOptionItemImageView);
        textView = convertView.findViewById(R.id.quoteOptionItemTv);
        imageView.setImageResource(spinnerNavItem.get(position).getIcon());
        textView.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }

}