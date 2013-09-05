package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import ca.taglab.PictureFrame.database.AddExistingPicture;
import ca.taglab.PictureFrame.database.AddPicture;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import java.util.Timer;
import java.util.TimerTask;

public class UserMainActivity extends Activity {

    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private final static String TAG = "UserMainActivity";
    
    private View mContact;
    private View mContactImage;
    private View mContactNew;
    private View mContactExisting;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_menu);

        getActionBar().hide();

        mContact = findViewById(R.id.add_contact);
        mContactImage = findViewById(R.id.contact);
        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactImage.setVisibility(View.GONE);
                mContactNew.setVisibility(View.VISIBLE);
                mContactExisting.setVisibility(View.VISIBLE);
            }
        });

        mContactNew = findViewById(R.id.contact_new);
        mContactNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddPicture.class));
            }
        });
        
        mContactExisting = findViewById(R.id.contact_existing);
        mContactExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddExistingPicture.class));
            }
        });
        
        
        findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, ScreenSlideActivity.class));
                
            }
        });

        
        Log.d(TAG, "Starting timer to retrieve unread emails...");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getUnreadEmails();
            }
        }, 0, REFRESH_INTERVAL);
    }

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }
}
