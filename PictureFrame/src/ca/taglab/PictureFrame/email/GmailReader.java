package ca.taglab.PictureFrame.email;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

public class GmailReader {
    
    private static final String TAG = "GmailReader";
    
    private String email;
    private String pwd;
    
    public GmailReader(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }
    
    public synchronized void readMail() throws Exception {
        
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        
        Session session = Session.getDefaultInstance(props, null);
        //session.setDebug(true);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", this.email, this.pwd);
        Log.d(TAG, store.toString());

        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);
        int numTotalMsgs = inbox.getMessageCount(); 
        int numUnreadMsgs = inbox.getUnreadMessageCount();
        Log.d(TAG, "Total Msgs: " + numTotalMsgs + " | Unread Msgs: " + numUnreadMsgs);
        
        //Flags seen = new Flags(Flags.Flag.SEEN);
        //FlagTerm ft = new FlagTerm(seen, false); // this is an unseen FlagTerm
        //Message messages[] = inbox.search(ft);
        
        Message messages[] = inbox.getMessages();
        Log.d(TAG, "Number of messages in the array: " + messages.length);
        
        int i = 0;
        for (Message message : messages) {
            // message.setFlag(Flags.Flag.ANSWERED, true);
            // message.setFlag(Flags.Flag.SEEN, true);
            
            int msgNum = message.getMessageNumber();
            String msgDate = message.getReceivedDate().toString();
            String msgFrom = message.getFrom()[0].toString();
            String msgSubject = message.getSubject();
            String msgBody = message.getContent().toString();

            Log.d(TAG, "==============Message " + (i + 1) + "==============");
            Log.d(TAG, "Email Num: " + msgNum);
            Log.d(TAG, "Date: " + msgDate);
            Log.d(TAG, "From: " + msgFrom);
            Log.d(TAG, "Subject: " + msgSubject);
            Log.d(TAG, "Body: " + msgBody);
            
            i++;
            
            //inbox.close(false);
            //store.close();

        }
    }

}
