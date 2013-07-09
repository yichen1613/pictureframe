package ca.taglab.PictureFrame.email;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.BuildConfig;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendEmailAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "SendEmailAsyncTask";
    
    Context ctx;
    
    private String senderEmail;
    private String senderPwd;
    private String recipients;
    private String subject;
    private String body;
    private String[] attachments;
    private GmailSender sender;

    public SendEmailAsyncTask(Context context, String senderEmail, String senderPwd, String recipients, String subject, String body, String[] attachments) {
        if (BuildConfig.DEBUG) Log.v(TAG, "SendEmailAsyncTask()");

        this.ctx = context;
        
        this.senderEmail = senderEmail;
        this.senderPwd = senderPwd;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;

        this.sender = new GmailSender(this.senderEmail, this.senderPwd);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(TAG, "doInBackground()");
        try {
            sender.sendMail(this.subject, this.body, this.senderEmail, this.recipients, this.attachments);
            return "Email sent successfully";
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Bad account details");
            e.printStackTrace();
            return "AuthenticationFailedException";
        } catch (MessagingException e) {
            Log.e(TAG, "Message to " + this.recipients + " failed");
            e.printStackTrace();
            return "MessagingException";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        }
    }

    protected void onPostExecute(String result){
        if (result.equalsIgnoreCase("Email sent successfully")) {
            // TODO: Fix ScreenSlidePageFragment so that the success checkmark + sound is only displayed here
        } else if (result.equalsIgnoreCase("AuthenticationFailedException")) {
            Toast toast = Toast.makeText(ctx, "Your email or password is invalid. Please log in again.", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();
        } else if (result.equalsIgnoreCase("MessagingException")) {
            Toast.makeText(ctx, "Email to " + this.recipients + " failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx, "A general error occurred", Toast.LENGTH_LONG).show();
        }
    }
    
}