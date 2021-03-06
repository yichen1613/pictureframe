package ca.taglab.PictureFrame.email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.BuildConfig;
import ca.taglab.PictureFrame.LoginActivity;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.database.ObscuredSharedPreferences;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import javax.mail.AuthenticationFailedException;
import javax.mail.FolderClosedException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.NoSuchProviderException;

public class ReadEmailAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "ReadEmailAsyncTask";

    Context ctx;

    private String mEmail;
    private String mPwd;
    private String mFlags;

    public ReadEmailAsyncTask(Context context, String flags) {
        if (BuildConfig.DEBUG) Log.v(TAG, "ReadEmailAsyncTask()");

        this.ctx = context;
        this.mFlags = flags;

        SharedPreferences prefs = new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE));
        this.mEmail = prefs.getString("email", "");
        this.mPwd = prefs.getString("password", "");
    }

    @Override
    protected String doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(TAG, "doInBackground()");
        try {
            GmailReader reader = new GmailReader(this.ctx, this.mEmail, this.mPwd, this.mFlags);
            reader.readMail();
            return "Emails retrieved successfully";
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Invalid credentials");
            e.printStackTrace();
            return "AuthenticationFailedException";
        } catch (NoSuchProviderException e) {
            Log.e(TAG, "Session attempted to instantiate a Provider that does not exist");
            e.printStackTrace();
            return "NoSuchProviderException";
        } catch (FolderClosedException e) {
            Log.e(TAG, "Lost folder connection to server");
            e.printStackTrace();
            return "FolderClosedException";
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
        
        if (result.equalsIgnoreCase("Emails retrieved successfully")) {
            //Toast.makeText(ctx, "Emails retrieved successfully!", Toast.LENGTH_LONG).show();
            
            if (this.mFlags.equals("UNREAD")) {
                // Do query on MessageTable for COL_READ = 0 rows. These are unread messages that need to be notified to user!
                String mSelectionClause = MessageTable.COL_READ + "=0";
                Cursor mCursor = ctx.getContentResolver().query(UserContentProvider.MESSAGE_CONTENT_URI, MessageTable.PROJECTION, mSelectionClause, null, MessageTable.COL_ID);

                int msg_id;
                if (mCursor != null) {
                    Log.d(TAG, "Number of unread messages (parts): " + mCursor.getCount());
                    
                    while (mCursor.moveToNext()) {
                        msg_id = mCursor.getInt(mCursor.getColumnIndex(MessageTable.COL_ID));
                        Log.d(TAG, "Unread msg ID: " + msg_id);
                    }
                } else {
                    Log.d(TAG, "No unread messages were found");
                }
                mCursor.close();
            }
            
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
        
        } else if (result.equalsIgnoreCase("FolderClosedException")) {
            Toast.makeText(ctx, "Network error: Lost connection to server", Toast.LENGTH_LONG).show();
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