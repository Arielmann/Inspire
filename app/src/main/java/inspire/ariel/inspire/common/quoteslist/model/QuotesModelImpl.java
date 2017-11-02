package inspire.ariel.inspire.common.quoteslist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inspire.ariel.inspire.common.quoteslist.Quote;

public class QuotesModelImpl implements QuotesModel {

    /*
    * This singleton model manages the loading of
    * quotes and response to runtime changes
    */

    private static QuotesModelImpl quotesModel;
    private static List<Quote> dataSet = new ArrayList<>();
    private static Map<String, Quote> quotesMap;

    public static QuotesModelImpl getInstance() {
        if (quotesModel == null) {
            quotesModel = new QuotesModelImpl();
            quotesMap = new HashMap<>();
            quotesModel.initDataSet();
        }
        return quotesModel;
    }

    private QuotesModelImpl() {
    }

    @Override
    public void setDataSet(List<Quote> dataSet) {
        QuotesModelImpl.dataSet = dataSet;
    }

    @Override
    public List<Quote> getDataSet() {
        return dataSet;
    }

    private List initDataSet() { //Since singleton hashMap is use to determine dataSet, each change in the hashMap has to provoke this method
        Quote quote1 = new Quote();
        quote1.setMessage("Better one bird at hand than 2 on the tree");
        Quote quote2 = new Quote();
        quote2.setMessage("Be yourself, everybody else is already taken");
        Quote quote3 = new Quote();
        quote3.setMessage("The truth is always one thought away...");
        dataSet.add(quote1);
        dataSet.add(quote2);
        dataSet.add(quote3);
        return dataSet;
    }

    private void insertQuotesToDataStructures(Quote quote){
        quotesMap.put(quote.getObjectId(), quote);
        dataSet.add(quote);
    }

    private boolean isGroupInDataBase(String id) {
        if (quotesMap.get(id) != null) {
            return true;
        } else {
            Quote quoteroup = null; //TODO: use this comment code instead of null: DataBaseManager.getInstance(context).getContactedStylistsReader().getActionGroup(name);
            if (quoteroup != null) {
                insertQuotesToDataStructures(quoteroup);
                return true;
            }
            return false;
        }
    }

}


