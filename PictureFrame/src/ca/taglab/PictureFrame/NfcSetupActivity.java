package ca.taglab.PictureFrame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NfcSetupActivity extends Activity {

    private final static String TAG = "NfcSetupActivity";

    private EditText email;
    private EditText password;

    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    Tag tag;

    AlertDialog alertDialog;
    private String mCredentials;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_setup);
        getActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
    }

    public void writeTag(View v) {
        
        String mEmail = email.getText().toString().toLowerCase().trim();
        String mPassword = password.getText().toString().trim();

        if (!mEmail.isEmpty() && !mPassword.isEmpty()) {
            if (isValidEmail(mEmail)) {
                
                this.mCredentials = mEmail.concat("::").concat(mPassword);

                // When an NFC tag comes into range, call the activity that handles writing data to the tag
                adapter = NfcAdapter.getDefaultAdapter(this);

                // create pending intent as FLAG_ACTIVITY_SINGLE_TOP so that as NFC tags are tapped, multiple instances of the same app are NOT created
                pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                // find a tag
                IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                writeTagFilters = new IntentFilter[] { tagDetected };


                // TODO: Replace AlertDialog with a prompt image that covers the current screen (so user cannot type or click "Write Tag" button again)
                // Prompt user to place tag against device
                alertDialog = new AlertDialog.Builder(NfcSetupActivity.this).create();
                alertDialog.setTitle("Temporary Dialog"); // set Dialog Title
                alertDialog.setMessage("Please place NFC tag against the device"); // set Dialog Message        
                // alertDialog.setIcon(R.drawable.tick); // set Icon to Dialog
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //adapter.disableForegroundDispatch(getParent());
                    }
                });
                alertDialog.show(); // show AlertDialog

                // set up listener for the intent we are filtering for, s.t. when it detects an intent matching the intent filter, it calls onNewIntent()
                adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
                
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
    }

    public void cancel(View v) {
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
        finish();
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


    /**
     * Called when intent matches IntentFilter
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag detected! " + tag.toString(), Toast.LENGTH_LONG).show();

            writeToTag();
            alertDialog.dismiss();
            finish();
        }
    }

    /**
     * Write credentials to the tag
     */
    public void writeToTag() {
        Log.d(TAG, "Writing: " + mCredentials + " to NFC tag...");
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag not detected", Toast.LENGTH_LONG).show();
            } else {
                writeRecord(mCredentials, tag);
                //TODO: Replace Toast with success image + sound
                Toast.makeText(this, "Credentials successfully written to tag!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error during writing (tag may be too far from device)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, "Error during writing (tag may be too far from device)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Error during writing (tag may be too far from device)", Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

}
