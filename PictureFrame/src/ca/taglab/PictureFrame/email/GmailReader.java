package ca.taglab.PictureFrame.email;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;
import com.sun.mail.imap.IMAPMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

public class GmailReader {
    
    private static final String TAG = "GmailReader";
    
    private Context ctx;
    private String email;
    private String pwd;
    private String flags;

    public GmailReader(Context ctx, String email, String pwd, String flags) {
        this.ctx = ctx;
        this.email = email;
        this.pwd = pwd;
        this.flags = flags;
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
        
        Message messages[] = null;
        if (flags.equals("ALL")) {
            // get all messages
            Log.d(TAG, "Retrieving ALL messages...");
            messages = inbox.getMessages();
        } else if (flags.equals("UNREAD")) {
            // get only unread messages
            Log.d(TAG, "Retrieving UNREAD messages");
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm ft = new FlagTerm(seen, false); // unseen FlagTerm
            messages = inbox.search(ft);
        } else {
            // do nothing
        }
        Log.d(TAG, "Number of messages in the array: " + messages.length);
        
        int i = 0;
        for (Message message : messages) {            
            // use PEEK variant of FETCH when fetching message content
            ((IMAPMessage)message).setPeek(true);

            int msgNum = message.getMessageNumber();
            String msgDate = message.getReceivedDate().toString().trim();
            //String msgFrom = message.getFrom()[0].toString(); // returns "John Doe <john.doe@gmail.com>"
            Address[] from_array = message.getFrom();
            String msgFrom = (from_array == null ? null : ((InternetAddress) from_array[0]).getAddress()).toLowerCase().trim(); // returns "john.doe@gmail.com"
            String msgSubject = message.getSubject().trim();
            if (msgSubject == null) {
                msgSubject = "(no subject)";
            }

            Log.d(TAG, "==============Message " + (i + 1) + "==============");
            Log.d(TAG, "Email Num: " + msgNum);
            Log.d(TAG, "Date: " + msgDate);
            Log.d(TAG, "From: " + msgFrom);
            int from_uid = queryForUserId(msgFrom);
            // TODO: If the "from" email matches a user in the UserTable, continue processing the message and insert it in the MessageTable. Otherwise, skip it.
            if (from_uid != 0) {
                // found matching user - continue processing the message
                Log.d(TAG, "From userID: " + from_uid);
            }
            Log.d(TAG, "Subject: " + msgSubject);
            
            /**
            // Get header information
            Enumeration headers = message.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header h = (Header) headers.nextElement();
                Log.d(TAG, h.getName() + ": " + h.getValue());
            }
             */

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
            String s = msgBody.toString();
            s = trimThreadedMessages(s);
            Log.d(TAG, "PLAIN TEXT body: " + s);
            msgBodyFinal = msgBodyFinal.concat(s);
            
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
                        File folder = new File(Environment.getExternalStorageDirectory() + "/PictureFrame/Downloads");
                        boolean isFolderCreated = true;
                        if (!folder.exists()) {
                            Log.d(TAG, "Creating folder...");
                            isFolderCreated = folder.mkdirs();
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
                        String s = bp.getContent().toString();
                        s = trimThreadedMessages(s);
                        Log.d(TAG, "MULTIPART body #" + j + ": " + s);
                        msgBodyFinal = msgBodyFinal.concat(s);
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

    
    /**
     * Insert message into database
     */
    public void insertMessageIntoDb(String type, String datetime, String subject, String body, int to_id, int from_id) {
        ContentValues values = new ContentValues();
        values.put(MessageTable.COL_TYPE, type);
        values.put(MessageTable.COL_DATETIME, datetime);
        values.put(MessageTable.COL_SUBJECT, subject);
        values.put(MessageTable.COL_BODY, body);
        values.put(MessageTable.COL_TO_ID, to_id);
        values.put(MessageTable.COL_FROM_ID, from_id);
        ctx.getContentResolver().insert(UserContentProvider.MESSAGE_CONTENT_URI, values);
    }

    
    /**
     * Query for user ID matching the given email address
     */
    public int queryForUserId(String email) {
        int uid = 0;
        String mSelectionClause = UserTable.COL_EMAIL + "=\"" + email + "\"";
        Cursor mCursor = ctx.getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, mSelectionClause, null, UserTable.COL_ID);
        
        if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() == 1) {
            int index = mCursor.getColumnIndex(UserTable.COL_ID);
            uid = mCursor.getInt(index);
        } else {
            Log.d(TAG, "queryForUserId(): No user matching the given email was found");
        }
        
        mCursor.close();
        return uid;
    }

    
    /**
     * Hack to exclude threaded messages sent from Gmail
     */
    public String trimThreadedMessages(String s) {
        int pos;
        if ((pos = s.indexOf("On 2013-")) > 0) {
            s = s.substring(0, pos);
        } else if ((pos = s.indexOf("2013/")) > 0) {
            s = s.substring(0, pos);
        }
        return s;
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
