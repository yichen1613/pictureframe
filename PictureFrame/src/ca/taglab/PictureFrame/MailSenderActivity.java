package ca.taglab.PictureFrame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MailSenderActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);

        Button send = (Button) this.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    new SendEmailAsyncTask("anselina.chia@gmail.com", "PictureFrame: I'm thinking of you", "This is the body text", "/sdcard/1.jpg").execute();
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        });
    }
}