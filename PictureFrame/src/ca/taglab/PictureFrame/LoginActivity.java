package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.ObscuredSharedPreferences;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends Activity {

    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private final static String TAG = "LoginActivity";
    
    private EditText email;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

    }

    public void login(View v) {
        
        String mEmail = email.getText().toString().toLowerCase().trim();
        String mPassword = password.getText().toString().trim();
        
        // store email, password in SharedPreferences object after encryption
        SharedPreferences.Editor e = (new ObscuredSharedPreferences(this, this.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE))).edit();
        e.putString("email", mEmail);
        e.putString("password", mPassword);
        
        if (!mEmail.isEmpty() && !mPassword.isEmpty()) {
            if (isValidEmail(mEmail)) {
                e.commit();

                int uid = queryForUserId(mEmail);
                if (uid == -1) {
                    // User does not exist in UserTable, so insert the user

                    // Find the last row ID in the UserTable - the user we're inserting will be +1
                    int last_uid = getLastInsertedId();
                    
                    ContentValues values = new ContentValues();
                    values.put(UserTable.COL_NAME, "User");
                    values.put(UserTable.COL_EMAIL, mEmail);
                    values.put(UserTable.COL_IMG, "none");
                    values.put(UserTable.COL_PASSWORD, "1234");
                    values.put(UserTable.COL_OWNER_ID, last_uid + 1);
                    Log.d(TAG, "Inserted owner ID is: " + (last_uid + 1));
                    getContentResolver().insert(UserContentProvider.USER_CONTENT_URI, values);
                } else {
                    // do nothing (since nothing needs to be updated)
                }

                Toast.makeText(this, "Retrieving unread emails...", Toast.LENGTH_LONG);
                Log.d(TAG, "Retrieving unread emails...");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        getUnreadEmails();
                    }
                }, 0, REFRESH_INTERVAL);

                setResult(RESULT_OK);
                finish();
            } else {
                Toast toast = Toast.makeText(this, "Invalid email address", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(30);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this, "Blank fields are not allowed", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();
        }
    }

    /**
    @Override
    public void onBackPressed() {
        // do nothing on back press
    }
    */

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }

    /**
     * Return the user ID matching the given email address. Otherwise, return -1 if matching user does not exist.
     */
    public int queryForUserId(String email) {
        int uid = -1;
        String mSelectionClause = UserTable.COL_EMAIL + "=\"" + email + "\"";
        Cursor mCursor = getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, mSelectionClause, null, UserTable.COL_ID);

        if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() == 1) {
            int index = mCursor.getColumnIndex(UserTable.COL_ID);
            uid = mCursor.getInt(index);
        } else {
            Log.d(TAG, "queryForUserId(): No user matching the given email was found");
        }

        mCursor.close();
        return uid;
    }

    /**
     * Get row ID of the last inserted entry in the UserTable.
     */
    public int getLastInsertedId() {
        int id = 0;
        Cursor mCursor = getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, null, null, UserTable.COL_ID);
        if (mCursor != null && mCursor.moveToLast()) {
            id = mCursor.getInt(mCursor.getColumnIndex(UserTable.COL_ID));
        }
        return id;
    }

    /**
     * Check if the email address is valid
     */
    public static boolean isValidEmail(String email) {
        boolean isValid = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            isValid = false;
        }
        return isValid;
    }
    
}
