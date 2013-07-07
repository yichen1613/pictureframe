package ca.taglab.PictureFrame.email;

import android.os.AsyncTask;
import android.util.Log;
import ca.taglab.PictureFrame.BuildConfig;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = "SendEmailAsyncTask";
    
    private String recipients;
    private String subject;
    private String body;
    private String[] attachments;

    GmailSender sender = new GmailSender("blair.intouch@gmail.com", "familiesintouch");

    public SendEmailAsyncTask(String recipients, String subject, String body, String[] attachments) {
        if (BuildConfig.DEBUG) Log.v(TAG, "SendEmailAsyncTask()");

        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(TAG, "doInBackground()");
        try {
            sender.sendMail(this.subject, this.body, "blair.intouch@gmail.com", this.recipients, this.attachments);
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(TAG, "Message to " + this.recipients + " failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}