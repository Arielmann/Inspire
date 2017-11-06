package inspire.ariel.inspire.common.quoteslist.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.backendless.IDataStore;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.constants.AppStrings;


public class QuoteUploadService extends IntentService {

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Map> leadersStorage;


    private static final String TAG = QuoteUploadService.class.getSimpleName();

    public QuoteUploadService() {
        super(QuoteUploadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
