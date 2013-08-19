package ca.taglab.PictureFrame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ca.taglab.PictureFrame.database.AddExistingPicture;
import ca.taglab.PictureFrame.database.AddPicture;
import ca.taglab.PictureFrame.database.DatabaseHelper;

public class MyActivity extends ListActivity {
    private static Sample[] mSamples;
    
    private static final int START_USER_MAIN_SCREEN = 100;

    private class Sample {
        private CharSequence title;
        private Class<? extends Activity> activityClass;

        public Sample(int titleResId, Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
            this.title = getResources().getString(titleResId);
        }

        @Override
        public String toString() {
            return title.toString();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSamples = new Sample[] {
                new Sample(R.string.title_user_main, UserMainActivity.class),
                new Sample(R.string.title_log_in, LoginActivity.class),
                new Sample(R.string.title_nfc_setup, NfcSetupActivity.class),
                new Sample(R.string.title_receive_mail, ReceiveMailActivity.class)
        };

        setListAdapter(new ArrayAdapter<Sample>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                mSamples));

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        
        // Check that the user has logged in first
        SharedPreferences prefs = getSharedPreferences("ca.taglab.PictureFrame", MODE_PRIVATE);
        String mSenderEmail = prefs.getString("email", "");
        String mSenderPwd = prefs.getString("password", "");

        if (mSenderEmail.isEmpty() || mSenderPwd.isEmpty()) {
            
            Toast toast = Toast.makeText(this, "Please log in via NFC or keyboard first!", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();
             
            //startActivityForResult(new Intent(this, LoginActivity.class), START_USER_MAIN_SCREEN);
        }
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Launch the activity associated with this list position
        startActivity(new Intent(MyActivity.this, mSamples[position].activityClass));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case START_USER_MAIN_SCREEN:
                startActivity(new Intent(this, UserMainActivity.class));
        }
    }
}