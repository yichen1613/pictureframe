package ca.taglab.PictureFrame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;

public class ReceiveMailActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_mail);
        getActionBar().hide();
        
    }
    
    public void retrieveMail(View v) {
        new ReadEmailAsyncTask(this).execute();
    }
    
    public void refreshMail(View v) {
        // to be implemented - check for any unread emails
    }
    
    public void cancel(View v) {
        finish();
    }
}
