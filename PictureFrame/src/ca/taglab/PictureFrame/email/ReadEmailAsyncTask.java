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
import java.io.IOException;
import java.security.NoSuchProviderException;

public class ReadEmailAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "ReadEmailAsyncTask";

    Context ctx;

    private String mEmail;
    private String mPwd;

    public ReadEmailAsyncTask(Context context) {
        if (BuildConfig.DEBUG) Log.v(TAG, "ReadEmailAsyncTask()");

        this.ctx = context;

        SharedPreferences prefs = new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE));
        this.mEmail = prefs.getString("email", "");
        this.mPwd = prefs.getString("password", "");
    }

    @Override
    protected String doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(TAG, "doInBackground()");
        try {
            GmailReader reader = new GmailReader(this.mEmail, this.mPwd);
            reader.readMail();
            return "Emails read successfully";
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Invalid credentials");
            e.printStackTrace();
            return "AuthenticationFailedException";
        } catch (NoSuchProviderException e) {
            Log.e(TAG, "Session attempted to instantiate a Provider that does not exist");
            e.printStackTrace();
            return "NoSuchProviderException";
        } catch (MessagingException e) {
            Log.e(TAG, "Email retrieval from " + this.mEmail + " failed");
            e.printStackTrace();
            return "MessagingException";
        } catch (IOException e) {
            Log.e(TAG, "Failed or interrupted I/O operation(s)");
            e.printStackTrace();
            return "IOException";
        } catch (Exception e) {
            Log.e(TAG, "Caught exception in doInBackground()");
            e.printStackTrace();
            return "Exception";
        }
    }


    protected void onPostExecute(String result){
        
        if (result.equalsIgnoreCase("Emails read successfully")) {
            //Toast.makeText(ctx, "Emails read successfully! Email: " + this.mEmail + ", password: " + this.mPwd, Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("AuthenticationFailedException")) {
            Toast toast = Toast.makeText(ctx, "Your email or password is invalid. Please log in again.", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();

            Intent intent = new Intent(ctx, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);

        } else if (result.equalsIgnoreCase("NoSuchProviderException"))  {
            Toast.makeText(ctx, "Provider error: Provider does not exist", Toast.LENGTH_LONG).show();
        }
        
        else if (result.equalsIgnoreCase("MessagingException")) {
            Toast toast = Toast.makeText(ctx, "Network error: Email retrieval from " + this.mEmail + " failed", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();

        } else {
            Toast.makeText(ctx, "A general error occurred", Toast.LENGTH_LONG).show();
        }
    }

}