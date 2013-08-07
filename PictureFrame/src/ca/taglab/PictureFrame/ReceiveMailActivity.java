package ca.taglab.PictureFrame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;

import java.util.Timer;
import java.util.TimerTask;

public class ReceiveMailActivity extends Activity {

    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_mail);
        getActionBar().hide();
        
    }
    
    public void retrieveMail(View v) {
        new ReadEmailAsyncTask(this, "ALL").execute();
    }
    
    public void refreshMail(View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getUnreadEmails();
            }
        }, 0, REFRESH_INTERVAL);
    }
    
    public void readMail(View v) {
        // to be implemented
    }
    
    public void cancel(View v) {
        finish();
    }

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }
}
