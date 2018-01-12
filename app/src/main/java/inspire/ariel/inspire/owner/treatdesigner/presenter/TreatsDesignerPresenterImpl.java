package inspire.ariel.inspire.owner.treatdesigner.presenter;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenterImpl;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkChecker;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;
import inspire.ariel.inspire.owner.treatdesigner.model.TreatDesignerModel;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsDesignerViewController;
import lombok.Getter;

public class TreatsDesignerPresenterImpl implements TreatsDesignerPresenter {

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_TREATS)
    IDataStore<Treat> treatsStorage;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> usersStorage;

    @Inject
    TreatDesignerModel model;

    @Inject
    NetworkChecker networkChecker;

    private String TAG = TreatsListPresenterImpl.class.getName();
    private TreatsDesignerViewController treatDesignerViewController;

    @Inject
    public TreatsDesignerPresenterImpl(AppComponent component, TreatsDesignerViewController treatDesignerViewController) {
        component.inject(this);
        this.treatDesignerViewController = treatDesignerViewController;
    }

    //==============================================================================================
    //  Lifecycle methods
    //==============================================================================================


    @Override
    public void onDestroy() {
        treatDesignerViewController = null;
    }

    //==============================================================================================
    //  Getters
    //==============================================================================================

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

    //==============================================================================================
    //  Background images methods
    //==============================================================================================

    @Getter
    private DiscreteScrollView.OnItemChangedListener onItemChangedListener = (viewHolder, adapterPosition) -> willSetBackgroundImage(adapterPosition);

    private void willSetBackgroundImage(int position) {
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            treatDesignerViewController.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
        }
    }

    //==============================================================================================
    //  Treat validation
    //==============================================================================================

    public boolean validateTreatForUpload(String text) {
        if (text.replaceAll(AppStrings.REGEX_FIND_WHITESPACES, AppStrings.EMPTY_STRING).isEmpty()) {
            treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_empty_treat));
            return false;
        }

        if (!NetworkChecker.getInstance().hasNetworkAccess(treatDesignerViewController.getContext())) {
            treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_no_connection));
            return false;
        }

        if (!networkChecker.hasNetworkAccess(treatDesignerViewController.getContext())) {
            treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    //==============================================================================================
    //  Serve communication
    //==============================================================================================

    //TODO: Protect from using the app id to post as this user from a rouge device
    @Override
    public void requestPostTreat(Treat treat) {
        if (!validateTreatForUpload(treat.getText())) {
            return;
        }
        postTreat(treat);
    }

    private void postTreat(Treat treat) {
        treatDesignerViewController.showProgressDialog();
        treatsStorage.save(treat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat treat) {
                model.insertTreatToDb(treat);
                sendPushNotification(DataManager.getInstance().getUser(), treat);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error saving treat to server: " + fault.getDetail());
                if (fault.getCode().equals(AppStrings.BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR)) {
                    treatDesignerViewController.showErrorDialogAndGoBackToTreatListActivity();
                    return;
                }
                treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_treat_upload));
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
        treatDesignerViewController.showProgressDialog();
        treatsStorage.save(treat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat treat) {
                treatDesignerViewController.dismissProgressDialog();
                Intent intent = new Intent(treatDesignerViewController.getContext(), TreatsListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppStrings.KEY_TREAT, treat);
                intent.putExtra(AppStrings.KEY_TREAT_POSITION, position);
                treatDesignerViewController.sendResultToActivity(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error updating treat to server: " + fault.getDetail());
                if (fault.getCode().equals(AppStrings.BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR)) {
                    treatDesignerViewController.showErrorDialogAndGoBackToTreatListActivity();
                    return;
                }
                treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_treat_update));
            }
        });
    }

    /**
     * <pre>
     * Note for keys:
     *
     * NOTIFICATION_HEADER_CONTENT_TEXT - Text for push notification title
     *
     * KEY_TEXT - The actual treat's text for the app
     * </pre>
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
                treatDesignerViewController.dismissProgressDialog();
                treat.setCreated(new Date());
                Intent intent = new Intent(treatDesignerViewController.getContext(), TreatsListActivity.class);
                intent.putExtra(AppStrings.KEY_TREAT, treat);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                treatDesignerViewController.goToOtherActivity(intent);
                setAdminTreatRelation(user, Collections.singletonList(treat));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Push notification sending error: " + fault.getDetail());
                treat.setCreated(new Date());
                treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.push_notification_send_error));
                Intent intent = new Intent().putExtra(AppStrings.KEY_TREAT, treat);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                treatDesignerViewController.goToOtherActivity(intent);
                setAdminTreatRelation(user, Collections.singletonList(treat));
            }
        });
    }

    private void setAdminTreatRelation(BackendlessUser user, List<Treat> singleTreatInsideList) {
        usersStorage.addRelation(user, AppStrings.BACKENDLESS_TABLE_USER_COLUMN_TREATS, singleTreatInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Log.i(TAG, "Relation is set for treat: " + singleTreatInsideList.get(0).getText() + " and admin: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server error. Relation was not create. reason: " + fault.getDetail());
                        //treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(treatDesignerViewController.getResources().getString(R.string.error_treat_leader_relation_creation));
                    }
                });
    }

    //==============================================================================================
    //  Menu items click methods
    //==============================================================================================


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
        treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_finding_image));
        return 0;
    }
}

