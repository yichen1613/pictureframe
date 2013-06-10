package ca.taglab.PictureFrame;

import android.os.AsyncTask;
import android.util.Log;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private String recipients;
    private String subject;
    private String body;
    private String attachment_location;

    GmailSender sender = new GmailSender("blair.intouch@gmail.com", "familiesintouch");

    public SendEmailAsyncTask(String recipients, String subject, String body, String attachment_location) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");

        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachment_location = attachment_location;
//        String[] toArr = { "anselina.chia@gmail.com"};
//        m.setTo(toArr);
//        m.setFrom("from mail@gmail.com");
//        m.setSubject("Email from Android");
//        m.setBody("body.");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
        try {
            sender.sendMail(this.subject, this.body, "blair.intouch@gmail.com", this.recipients);
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}