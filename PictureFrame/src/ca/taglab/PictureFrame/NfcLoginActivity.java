package ca.taglab.PictureFrame;

import android.app.Activity;
import android.os.Bundle;

public class NfcLoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_login);
        getActionBar().hide();
    }
    
    // When user taps with tag on any screen of the app, launch this activity, and retrieve/handle/process the tag's data:
    // Load to SharedPreferences object
    // Insert user into db if not already in it
    // Start retrieving unread emails
    // Display confirmation of login/success
    // Redirect user to PFrame
}
