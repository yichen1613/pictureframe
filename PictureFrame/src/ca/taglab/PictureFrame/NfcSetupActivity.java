package ca.taglab.PictureFrame;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.ObscuredSharedPreferences;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class NfcSetupActivity extends Activity {

    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private final static String TAG = "NfcSetupActivity";

    private EditText email;
    private EditText password;

    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag tag;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_setup);
        getActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        Log.d(TAG, "Creating IntentFilter...");
        // Create an IntentFilter. Alternatively, register our app directly with IntentFilter in the manifest (but wouldn't be able to launch our Activity regularly - Android would start up Activity when it detects a tag)
        // Android calls onNewIntent after detecting and deciding which app should process the tag
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    public void login(View v) {

        // TODO: Change Login button: 1) Prompt user ("Place NFC tag against ___", image/animation of tapping motion), 2) Write the Strings to NFC tag ("Writing tag...", "Success!")
        
        String mEmail = email.getText().toString().toLowerCase().trim();
        String mPassword = password.getText().toString().trim();
        String mCredentials = mEmail.concat(",").concat(mPassword);

        Log.d(TAG, "Writing: " + mCredentials + " to NFC tag...");
        
        // Try writing the credentials to the tag
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag not detected", Toast.LENGTH_LONG).show();
            } else {
                writeRecord(mCredentials, tag);
                Toast.makeText(this, "Credentials successfully written to tag!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error during writing (tag may be too far from device)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, "Error during writing (tag may be too far from device)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        
        /**
        // store email, password in SharedPreferences object after encryption
        SharedPreferences.Editor e = (new ObscuredSharedPreferences(this, this.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE))).edit();
        e.putString("email", mEmail);
        e.putString("password", mPassword);

        if (!mEmail.isEmpty() && !mPassword.isEmpty()) {
            if (isValidEmail(mEmail)) {
                e.commit();

                int uid = queryForUserId(mEmail);
                if (uid == 0) {
                    // User does not exist in UserTable, so insert the user
                    ContentValues values = new ContentValues();
                    values.put(UserTable.COL_NAME, "User");
                    values.put(UserTable.COL_EMAIL, mEmail);
                    values.put(UserTable.COL_IMG, "none");
                    values.put(UserTable.COL_PASSWORD, "1234");
                    getContentResolver().insert(UserContentProvider.USER_CONTENT_URI, values);
                } else {
                    // do nothing (since nothing needs to be updated)
                }

                Toast.makeText(this, "Retrieving unread emails...", Toast.LENGTH_LONG);
                Log.d(TAG, "Retrieving unread emails...");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        getUnreadEmails();
                    }
                }, 0, REFRESH_INTERVAL);

                finish();
            } else {
                Toast toast = Toast.makeText(this, "Invalid email address", Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(30);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this, "Blank fields are not allowed", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toast.show();
        }
         */
    }

    public void cancel(View v) {
        finish();
    }

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }

    /**
     * Return the user ID matching the given email address. Otherwise, return 0 if matching user does not exist.
     */
    public int queryForUserId(String email) {
        int uid = 0;
        String mSelectionClause = UserTable.COL_EMAIL + "=\"" + email + "\"";
        Cursor mCursor = getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, mSelectionClause, null, UserTable.COL_ID);

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
     * Check if the email address is valid
     */
    public static boolean isValidEmail(String email) {
        boolean isValid = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            isValid = false;
        }
        return isValid;
    }
    
    
    
    // NFC stuff

    /**
     * 
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag detected!" + tag.toString(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Create NDEF record
     */
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return record;
    }

    
    /**
     * Write NDEF record as an NDEF Message
     */
    private void writeRecord(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        
        // Get an instance of Ndef for the tag
        Ndef ndef = Ndef.get(tag);
        
        // Enable I/O
        ndef.connect();
        
        // Write the message
        ndef.writeNdefMessage(message);
        
        // Close the connection
        ndef.close();
    }

    
    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
}
