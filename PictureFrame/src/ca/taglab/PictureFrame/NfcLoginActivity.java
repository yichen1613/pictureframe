package ca.taglab.PictureFrame;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.ObscuredSharedPreferences;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class NfcLoginActivity extends Activity {

    public static final String TAG = "NfcLoginActivity";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private Context ctx;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_login);
        getActionBar().hide();
        
        ctx = this;
        mTextView = (TextView) findViewById(R.id.text);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled");
        } else {
            mTextView.setText("Please place NFC tag against the device");
        }
        handleIntent(getIntent());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // activity must be resumed/in the foreground
        setupForegroundDispatch(this, mNfcAdapter);
    }
    
    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    /**
     * Called when intent matches IntentFilter (i.e. user places NFC tag against device)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
            
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }
    
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
    
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }



    /**
     * Background task for reading the data, so UI thread is NOT blocked while reading
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // tag does not support NDEF
                return null;
            }
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }
            return null;
        }
        
        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();
            // get Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            // get Language Code
            int languageCodeLength = payload[0] & 0063;
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");  e.g. "en"
            // get Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.contains("::")) {
                    String[] credentials = result.split("::");
                    String mEmail = credentials[0];
                    String mPassword = credentials[1];
                    mTextView.setText("Tag contains valid credentials");

                    // Check SharedPreferences object - if user is currently logged in, log them out. Else, log them in.
                    SharedPreferences prefs = new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE));
                    String loggedInEmail = prefs.getString("email", "");
                    if (loggedInEmail.equals(mEmail)) {
                        SharedPreferences.Editor e = (new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE))).edit();
                        e.putString("email", "");
                        e.putString("password", "");
                        e.commit();
                        
                        // redirect user to Login screen
                        Toast.makeText(ctx, "Logged out successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(NfcLoginActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        // Log the user in
                        // Load to SharedPreferences object
                        SharedPreferences.Editor e = (new ObscuredSharedPreferences(ctx, ctx.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE))).edit();
                        e.putString("email", mEmail);
                        e.putString("password", mPassword);
                        e.commit();

                        // Insert user into db if not already in it
                        int uid = queryForUserId(mEmail);
                        if (uid == -1) {
                            // User does not exist in UserTable, so insert the user
                            // Find the last row ID in the UserTable - the user we're inserting will be +1
                            int last_uid = getLastInsertedId();

                            ContentValues values = new ContentValues();
                            values.put(UserTable.COL_NAME, "User");
                            values.put(UserTable.COL_EMAIL, mEmail);
                            values.put(UserTable.COL_IMG, "none");
                            values.put(UserTable.COL_PASSWORD, "1234");
                            values.put(UserTable.COL_OWNER_ID, last_uid + 1);
                            Log.d(TAG, "Inserted owner ID is: " + (last_uid + 1));
                            getContentResolver().insert(UserContentProvider.USER_CONTENT_URI, values);
                        } else {
                            // do nothing (since nothing needs to be updated)
                        }

                        // Display confirmation of login/success
                        Toast.makeText(ctx, "Logged in successfully as: " + mEmail, Toast.LENGTH_LONG).show();

                        // Start retrieving unread emails
                        //Toast.makeText(ctx, "Retrieving unread emails...", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Retrieving unread emails...");
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                getUnreadEmails();
                            }
                        }, 0, REFRESH_INTERVAL);

                        // Redirect user to PFrame
                        startActivity(new Intent(NfcLoginActivity.this, UserMainActivity.class));
                        finish();
                    }
                } else {
                    mTextView.setText("The tag contains invalid credentials: " + result);   
                }
            }
        }

        public void getUnreadEmails() {
            new ReadEmailAsyncTask(ctx, "UNREAD").execute();
        }

        /**
         * Return the user ID matching the given email address. Otherwise, return -1 if matching user does not exist.
         */
        public int queryForUserId(String email) {
            int uid = -1;
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
         * Get row ID of the last inserted entry in the UserTable.
         */
        public int getLastInsertedId() {
            int id = 0;
            Cursor mCursor = getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, null, null, UserTable.COL_ID);
            if (mCursor != null && mCursor.moveToLast()) {
                id = mCursor.getInt(mCursor.getColumnIndex(UserTable.COL_ID));
            }
            return id;
        }
    }
    
}
