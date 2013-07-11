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
    
    public GmailReader() {
        
    }
    
    public void readMail() {
        
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        
        try {
            Session session = Session.getDefaultInstance(props, null);
            //session.setDebug(true);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "blair.intouch@gmail.com", "familiesintouch");
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
                
                /**
                String content = message.getContentType();
                MimeMultipart part = (MimeMultipart) message.getContent();

                BodyPart bodyPart = part.getBodyPart(0);
                part.getContentType();
                part.getCount();
                part.getPreamble();
                
                try {
                    printParts(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                */
                
                //inbox.close(false);
                //store.close();

            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }

    /**
    public static void printParts(Part p) throws Exception {
        Object o = p.getContent();
        
        
        if (o instanceof String) {
            System.out.println("This is a String");
            System.out.println((String) o);
        } else if (o instanceof Multipart) {
            System.out.println("This is a Multipart");
            Multipart mp = (Multipart) o;
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                printParts(mp.getBodyPart(i));
            }
        } else if (o instanceof InputStream) {
            System.out.println("This is just an input stream");
            InputStream is = (InputStream) o;
            int c;
//            while ((c = is.read()) != -1)
//                System.out.write(c);
        }
        
    }
     */

}
