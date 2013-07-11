package ca.taglab.PictureFrame.email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.BuildConfig;
import ca.taglab.PictureFrame.LoginActivity;
import ca.taglab.PictureFrame.database.ObscuredSharedPreferences;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ReadEmailAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "ReadEmailAsyncTask";

    Context ctx;

    /**
    private String senderEmail;
    private String senderPwd;
    private String recipients;
    private String subject;
    private String body;
    private String[] attachments;
     */

    public ReadEmailAsyncTask(Context context) {
        if (BuildConfig.DEBUG) Log.v(TAG, "ReadEmailAsyncTask()");

        this.ctx = context;
        /**
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;

        SharedPreferences prefs = new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE));
        this.senderEmail = prefs.getString("email", "");
        this.senderPwd = prefs.getString("password", "");
         */
    }

    @Override
    protected String doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(TAG, "doInBackground()");
        try {
            GmailReader reader = new GmailReader();
            reader.readMail();
            return "Emails read successfully";
        } catch (Exception e) {
            Log.e(TAG, "Caught exception in doInBackground()");
            e.printStackTrace();
            return "Exception";
        }
    }


    protected void onPostExecute(String result){

        if (result.equalsIgnoreCase("Emails read successfully")) {
            // Checkmark + success sound should only be displayed upon this result (fix ScreenSlidePageFragment)?
            Toast.makeText(ctx, "Emails read successfully!", Toast.LENGTH_LONG).show();

        } /**else if (result.equalsIgnoreCase("AuthenticationFailedException")) {
            Toast toast = Toast.makeText(ctx, "Your email or password is invalid. Please log in again.", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();

            Intent intent = new Intent(ctx, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);

        } else if (result.equalsIgnoreCase("MessagingException")) {
            Toast toast = Toast.makeText(ctx, "Network error: Email to " + this.recipients + " failed", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();

        }*/ else {
            Toast.makeText(ctx, "A general error occurred", Toast.LENGTH_LONG).show();
        }
    }

}