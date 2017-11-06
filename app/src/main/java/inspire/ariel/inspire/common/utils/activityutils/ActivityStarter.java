package inspire.ariel.inspire.common.utils.activityutils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import inspire.ariel.inspire.R;

public class ActivityStarter implements View.OnClickListener {

    private Context context;
    private Activity caller; //callback activity in activityForResult
    private int requestCode = 1; //used in activityForResult
    private Intent starter;
    private String dialogMessage;

    //Simply go to screen on button click
    private ActivityStarter(Context context, Class screen) {
        this.context = context;
        this.starter = new Intent(context, screen);
    }

    //Go to screen on button click with progress dialog
    private ActivityStarter(Activity activity, Class screen, String dialogMessage) {
        this.context = activity;
        this.starter = new Intent(activity, screen);
        this.dialogMessage = dialogMessage;
    }

    //Start activity for result on click
    private ActivityStarter(Activity caller, Class activityForResult, int requestCode) {
        this.caller = caller;
        this.starter = new Intent(caller, activityForResult);
        this.requestCode = requestCode;
    }

    @Override
    public void onClick(View v) {
        context.startActivity(starter);
    }

    private View.OnClickListener startActivityWithProgressDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ProgressDialog progressDialog = new ProgressDialog(context,
                    R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(dialogMessage);
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            context.startActivity(starter);
                        }
                    }, 2000);
        }
    };

    private View.OnClickListener startActivityForResultListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            caller.startActivityForResult(starter, requestCode);
        }
    };

    public static void setStartActivityOnClickListener(Button button, Context context, Class targetScreen) {
        ActivityStarter starter = new ActivityStarter(context, targetScreen);
        button.setOnClickListener(starter);
    }

    public static void setStartActivityForResultOnClickListener(Button button, Activity caller, Class activityForResult) {
        ActivityStarter starter = new ActivityStarter(caller, activityForResult, 1);
        button.setOnClickListener(starter.startActivityForResultListener);
    }

    public static void startActivity(Context context, Class targetScreen) {
        ActivityStarter starter = new ActivityStarter(context, targetScreen);
        starter.onClick(null);
    }

    public static void startActivityForResult(Activity caller, Class activityForResult){
        Intent intent = new Intent(caller, activityForResult);
        caller.startActivityForResult(intent, 1);
    }
}