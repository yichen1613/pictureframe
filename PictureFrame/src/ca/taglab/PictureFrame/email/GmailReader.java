package ca.taglab.PictureFrame.email;

import android.os.Environment;
import android.util.Log;
import com.sun.mail.imap.IMAPMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

public class GmailReader {
    
    private static final String TAG = "GmailReader";
    
    private String email;
    private String pwd;

    public GmailReader(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }
    
    public synchronized ArrayList<Msg> readMail() throws Exception {
        ArrayList<Msg> msgArrayList = new ArrayList<Msg>();
        
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        
        Session session = Session.getInstance(props, null);
        Log.d(TAG, session.toString());
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", this.email, this.pwd);
        Log.d(TAG, store.toString());

        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_WRITE);
        
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
            
            // use PEEK variant of FETCH when fetching message content
            ((IMAPMessage)message).setPeek(true);
            
            int msgNum = message.getMessageNumber();
            String msgDate = message.getReceivedDate().toString();
            String msgFrom = message.getFrom()[0].toString();
            String msgSubject = message.getSubject();
            if (msgSubject == null) {
                msgSubject = "(no subject)";
            }

            Log.d(TAG, "==============Message " + (i + 1) + "==============");
            Log.d(TAG, "Email Num: " + msgNum);
            Log.d(TAG, "Date: " + msgDate);
            Log.d(TAG, "From: " + msgFrom);
            Log.d(TAG, "Subject: " + msgSubject);

            Object msgBody = message.getContent();
            String msgBodyFinal = processMessage(msgBody, "");
            
            Msg msg = this.new Msg(msgNum, msgDate, msgFrom, msgSubject, msgBodyFinal);
            msgArrayList.add(msg);
            Log.d(TAG, "Number of msgs in msgArrayList: " + msgArrayList.size());
            
            // mark message as read
            inbox.setFlags(new Message[] {message}, new Flags(Flags.Flag.SEEN), true);
            
            i++;

        }
        
        // close connection
        inbox.close(false);
        store.close();
        
        return msgArrayList;
    }

    public String processMessage(Object msgBody, String prevMsgBody) throws Exception {
        
        String msgBodyFinal = prevMsgBody;
        Log.d(TAG, "...PROCESSING MESSAGE...");
        
        if (msgBody instanceof String) {
            // plain text message
            Log.d(TAG, "PLAIN TEXT body: " + msgBody.toString());
            msgBodyFinal = msgBodyFinal.concat(msgBody.toString());
            
        } else if (msgBody instanceof Multipart) {
            // multipart message
            Log.d(TAG, "...PROCESSING MULTIPART...");
            Multipart mp = (Multipart) msgBody;
            
            for (int j = 0; j < mp.getCount(); j++) {
                Part bp = mp.getBodyPart(j);
                String disposition = bp.getDisposition();

                if (disposition != null) {
                    if (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE)) {
                        Log.d(TAG, "******Email has attachment******");
                        String filename = bp.getFileName();

                        // Download/save the attachment in external storage
                        File folder = new File(Environment.getExternalStorageDirectory() + "/pictureframe");
                        boolean isFolderCreated = true;
                        if (!folder.exists()) {
                            Log.d(TAG, "Creating folder...");
                            isFolderCreated = folder.mkdir();
                        }
                        
                        if (isFolderCreated) {
                            String filepath = folder.getAbsolutePath() + "/" + filename;
                            File file = new File(filepath);
                            // Check if file with the same name already exists in the directory - if so, append a number
                            for (int i = 0; file.exists(); i++) {
                                int pos = filepath.lastIndexOf(".");
                                if (pos > 0) {
                                    String filepathWithoutExt = filepath.substring(0, pos);
                                    String fileExtension = filepath.substring(pos);
                                    String newFilepath = filepathWithoutExt + "_" + i + fileExtension;
                                    Log.d(TAG, "Old Filepath: " + filepath + ", Filepath without ext:" + filepathWithoutExt + ", File Extension: " + fileExtension + ", New Filepath: " + newFilepath);
                                    file = new File(newFilepath);
                                }
                            }
                            ((MimeBodyPart) bp).saveFile(file);
                            Log.d(TAG, "Saved the attachment to: " + file.getAbsolutePath());
                        } else {
                            Log.d(TAG, "Error: Folder was not found/created!");
                        }
                    }
                } else {
                    // Handle cases where message parts are NOT flagged appropriately
                    MimeBodyPart mbp = (MimeBodyPart) bp;
                    if (mbp.isMimeType("text/plain")) {
                        // Handle plain
                        Log.d(TAG, "isMimeType #" + j + ": text/plain");
                        Log.d(TAG, "MULTIPART body #" + j + ": " + bp.getContent().toString());
                        msgBodyFinal = msgBodyFinal.concat(bp.getContent().toString());
                    } else if (mbp.isMimeType("text/html")) {
                        Log.d(TAG, "isMimeType #" + j + ": text/html");
                    } else if (mbp.isMimeType("text/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": text/*");
                    } else if (mbp.isMimeType("message/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": message/*");
                    } else if (mbp.isMimeType("image/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": image/*");
                    } else if (mbp.isMimeType("video/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": video/*");
                    } else if (mbp.isMimeType("audio/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": audio/*");

                    } else if (mbp.isMimeType("application/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": application/*");
                    } else if (mbp.isMimeType("model/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": model/*");
                    } else if (mbp.isMimeType("multipart/*")) {
                        Log.d(TAG, "isMimeType #" + j + ": multipart/*");
                        msgBodyFinal = processMessage(bp.getContent(), msgBodyFinal);
                    } else {
                        Log.d(TAG, "isMimeType #" + j + " DID NOT MATCH! getContentType(): " + mbp.getContentType());
                    }
                }
            }
        }
        return msgBodyFinal;
    }
    
    
    class Msg {
        int mNum;
        String mDate;
        String mFrom;
        String mSubject;
        String mBody;
        
        public Msg(int num, String date, String from, String subject, String body) {
            this.mNum = num;
            this.mDate = date;
            this.mFrom = from;
            this.mSubject = subject;
            this.mBody = body;
        }
    }

}
