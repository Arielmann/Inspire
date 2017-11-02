package inspire.ariel.inspire.common.quoteslist.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.backendless.IDataStore;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.Leader;
import inspire.ariel.inspire.common.resources.AppStrings;

public class QuoteRetrievalService extends IntentService {

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

    public QuoteRetrievalService() {
        super(QuoteRetrievalService.class.getName());

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Leader leader = leadersStorage.findById(AppStrings.LEADER_DEVICE_ID);
    }
}
