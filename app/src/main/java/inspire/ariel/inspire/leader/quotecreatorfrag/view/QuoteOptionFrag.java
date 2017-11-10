package inspire.ariel.inspire.leader.quotecreatorfrag.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.databinding.FragQuoteOptionsBinding;

public class QuoteOptionFrag extends Fragment implements QuoteOptionView {

    private static final String TAG = QuoteOptionFrag.class.getSimpleName();
    private FragQuoteOptionsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_quote_options, container, false);
        binding.optionImgBtn.setOnClickListener(onImgButtonClicked);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        binding.optionRV.setLayoutManager(lm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.optionRV.getContext(),
                OrientationHelper.VERTICAL);
        binding.optionRV.addItemDecoration(dividerItemDecoration);
        binding.optionRV.setHasFixedSize(true);
        return binding.getRoot();
    }

    View.OnClickListener onImgButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(binding.optionExpandingLayout.isExpanded()){
                binding.optionExpandingLayout.collapse();
            }else{
                binding.optionExpandingLayout.expand();
            }
        }
    };

    @Override
    public RecyclerView getRV() {
        return binding.optionRV;
    }
}
