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
                    // To send an attachment (the body of the email will be empty)
                    new SendEmailAsyncTask("anselina.chia@gmail.com", "PictureFrame: You have a new image", "", "/sdcard/Attachments/Untitled-1.jpg").execute();

                    // To send a wave (set param attachment_location as an empty string)
                    //new SendEmailAsyncTask("anselina.chia@gmail.com", "PictureFrame: I'm thinking of you", "This is the body text", "").execute();
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        });
    }
}