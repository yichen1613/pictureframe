package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
            e.commit();
            
            // add user to UserTable
            ContentValues values = new ContentValues();
            values.put(UserTable.COL_NAME, "User");
            values.put(UserTable.COL_EMAIL, mEmail);
            values.put(UserTable.COL_IMG, "none");
            values.put(UserTable.COL_PASSWORD, "1234");
            getContentResolver().insert(UserContentProvider.USER_CONTENT_URI, values);

            finish();
            
            Toast.makeText(this, "Retrieving unread emails...", Toast.LENGTH_LONG);
            Log.d(TAG, "Retrieving unread emails...");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    getUnreadEmails();
                }
            }, 0, REFRESH_INTERVAL);
            
            finish();
        } else {
            Toast toast = Toast.makeText(this, "Blank fields are not allowed", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();
        }
    }

    public void cancel(View v) {
        finish();
    }

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }
    
}
