package inspire.ariel.inspire.owner.treatcreator.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenterImpl;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;
import inspire.ariel.inspire.dbmanager.RealmManager;
import inspire.ariel.inspire.owner.treatcreator.model.TreatCreatorModel;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewController;
import lombok.Getter;

public class TreatsCreatorPresenterImpl implements TreatsCreatorPresenter {

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_TREATS)
    IDataStore<Treat> treatsStorage;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> usersStorage;

    @Inject
    TreatCreatorModel model;

    @Inject
    NetworkHelper networkHelper;

    private String TAG = TreatsListPresenterImpl.class.getName();
    private TreatsCreatorViewController treatCreatorViewController;

    @Inject
    public TreatsCreatorPresenterImpl(AppComponent component, TreatsCreatorViewController treatCreatorViewController) {
        component.inject(this);
        this.treatCreatorViewController = treatCreatorViewController;
    }

    /**
     * Lifecycle Methods
     */

    @Override
    public void onDestroy() {
        treatCreatorViewController = null;
    }

    /**
     * Getters
     */
    @Override
    public String getBgImgName() {
        return model.getBgImageName();
    }

    @Override
    public String getFontPath() {
        return model.getFontPath();
    }

    @Override
    public Drawable getChosenBgImage() {
        return customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue());
    }

    /**
     * Background Image Methods
     */

    @Getter
    private DiscreteScrollView.OnItemChangedListener onItemChangedListener = (viewHolder, adapterPosition) -> willSetBackgroundImage(adapterPosition);

    private void willSetBackgroundImage(int position) {
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            treatCreatorViewController.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
        }
    }

    /**
     * Treat Validation
     */

    public boolean validateTreatForUpload(String text) {
        if (text.replaceAll(AppStrings.REGEX_FIND_WHITESPACES, AppStrings.EMPTY_STRING).isEmpty()) {
            treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_empty_treat));
            return false;
        }

        if (!NetworkHelper.getInstance().hasNetworkAccess(treatCreatorViewController.getContext())) {
            treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_no_connection));
            return false;
        }

        if (!networkHelper.hasNetworkAccess(treatCreatorViewController.getContext())) {
            treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    /**
     * Server Communication
     */

    //TODO: Protect from using the app id to post as this user from a rouge device
    @Override
    public void requestPostTreat(Treat treat) {
        if (!validateTreatForUpload(treat.getText())) {
            return;
        }
        postTreat(treat);
    }

    private void postTreat(Treat treat) {
        treatCreatorViewController.showProgressDialog();
        treatsStorage.save(treat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat treat) {
                RealmManager.getInstance().saveTreat(treat);
                sendPushNotification(DataManager.getInstance().getUser(), treat);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error saving treat to server: " + fault.getDetail());
                if (fault.getCode().equals(AppStrings.BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR)) {
                    treatCreatorViewController.showErrorDialogAndGoBackToTreatListActivity();
                    return;
                }
                treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_treat_upload));
            }
        });
    }

    @Override
    public void requestUpdateTreat(Treat treat, int position) {
        if (!validateTreatForUpload(treat.getText())) {
            return;
        }
        updateTreat(treat, position);
    }

    private void updateTreat(Treat treat, int position) {
        treatCreatorViewController.showProgressDialog();
        treatsStorage.save(treat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat treat) {
                treatCreatorViewController.dismissProgressDialog();
                Intent intent = new Intent(treatCreatorViewController.getContext(), TreatsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppStrings.KEY_TREAT, treat);
                intent.putExtra(AppStrings.KEY_TREAT_POSITION, position);
                treatCreatorViewController.sendResultToActivity(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error updating treat to server: " + fault.getDetail());
                if (fault.getCode().equals(AppStrings.BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR)) {
                    treatCreatorViewController.showErrorDialogAndGoBackToTreatListActivity();
                    return;
                }
                treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_treat_update));
            }
        });
    }

    /**
     * NOTE for keys:
     * NOTIFICATION_HEADER_CONTENT_TEXT - For presenting in notification
     * KEY_TEXT - The actual treat for the app
     */
    //TODO: Don't register user to it's own server channel or you get double newTreat on his machine! (until app restarts)
    private void sendPushNotification(BackendlessUser user, Treat treat) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_TICKER_TEXT, String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TITLE, String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TEXT, customResourcesProvider.getResources().getString(R.string.new_treat_push_notification));
        publishOptions.putHeader(AppStrings.KEY_OBJECT_ID, treat.getObjectId());
        publishOptions.putHeader(AppStrings.KEY_OWNER_ID, treat.getOwnerId());
        publishOptions.putHeader(AppStrings.KEY_TEXT, treat.getText());
        publishOptions.putHeader(AppStrings.KEY_FONT_PATH, treat.getFontPath());
        publishOptions.putHeader(AppStrings.KEY_TEXT_SIZE, String.valueOf(treat.getTextSize()));
        publishOptions.putHeader(AppStrings.KEY_TEXT_COLOR, String.valueOf(treat.getTextColor()));
        publishOptions.putHeader(AppStrings.KEY_BG_IMAGE_NAME, treat.getBgImageName());

        Backendless.Messaging.publish(AppStrings.VAL_OWNER_NAME, AppStrings.SPACE_STRING, publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                Log.i(TAG, "Message sent");
                treatCreatorViewController.dismissProgressDialog();
                treat.setCreated(new Date());
                Intent intent = new Intent(treatCreatorViewController.getContext(), TreatsListActivity.class);
                intent.putExtra(AppStrings.KEY_TREAT, treat);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                treatCreatorViewController.goToOtherActivity(intent);
                setUserTreatRelation(user, new ArrayList<Treat>() {{
                    add(treat);
                }});
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Push notification sending error: " + fault.getDetail());
                treat.setCreated(new Date());
                treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.push_notification_send_error));
                Intent intent = new Intent().putExtra(AppStrings.KEY_TREAT, treat);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                treatCreatorViewController.goToOtherActivity(intent);
                setUserTreatRelation(user, new ArrayList<Treat>() {{
                    add(treat);
                }});
            }
        });
    }

    private void setUserTreatRelation(BackendlessUser user, List<Treat> singleTreatInsideList) {
        usersStorage.addRelation(user, AppStrings.BACKENDLESS_TABLE_USER_COLUMN_TREATS, singleTreatInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Log.i(TAG, "Relation has been set startOperations treat: " + singleTreatInsideList.get(0).getText() + " and user: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server reported an error: " + fault.getDetail());
                        //treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(treatCreatorViewController.getResources().getString(R.string.error_treat_leader_relation_creation));
                    }
                });
    }

    /**
     * Menu Items Clicked Methods
     */

    @Override
    public void onTreatFontClicked(String path) {
        setFontPath(path);
    }

    @Override
    public void setFontPath(String path) {
        model.setFontPath(path);
    }

    @Override
    public int getImagePositionFromImageName(String bgImageName) {
        //TODO: Consult if changing to different data structure will reduce complexity
        int index = 0;
        for (InspireBackgroundImage inspireBackgroundImage : customResourcesProvider.getBackgroundImages()) {
            if (bgImageName.equalsIgnoreCase(inspireBackgroundImage.getName())) {
                return index;
            }
            index++;
        }

        Log.e(TAG, "Error getting image. Image name was not found in array. array details: " + customResourcesProvider.getBackgroundImages().toString() + " Image name: " + bgImageName);
        treatCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_finding_image));
        return 0;
    }
}

