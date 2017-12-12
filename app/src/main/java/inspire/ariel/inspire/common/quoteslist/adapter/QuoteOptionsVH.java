package inspire.ariel.inspire.common.quoteslist.adapter;

import android.content.Intent;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.events.OnQuoteDeleteClickedEvent;
import inspire.ariel.inspire.common.quoteslist.events.OnQuoteUpdatedEvent;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.databinding.VhQuoteOptionsBinding;
import inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity.QuoteEditorActivity;
import lombok.Getter;

public class QuoteOptionsVH extends ViewHolder {

    private VhQuoteOptionsBinding binding;
    @Getter private int height;

    public QuoteOptionsVH(VhQuoteOptionsBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        calculateLayoutHeight();
    }

    /**
     * Note: The total size is calculated based on each property within
     * the relative layout. All new properties height should
     * confirm to this fixed height(R.dimen.quote_option_height)
     */
    private void calculateLayoutHeight(){
        for (int i = 0; i < binding.quoteOptionsManagerLayout.getChildCount(); i++) {
            height += binding.getRoot().getResources().getDimension(R.dimen.quote_option_height);
        }
    }

    public void init(DialogPlus dialog, Quote quote, int position) {
        binding.editBtn.setOnClickListener(view -> {
            EventBus.getDefault().post(new OnQuoteUpdatedEvent(quote, position));
            dialog.dismiss();
        });

        binding.deleteBtn.setOnClickListener(view -> {
            EventBus.getDefault().post(new OnQuoteDeleteClickedEvent(position));
            dialog.dismiss();
        });
    }
}
