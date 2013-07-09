package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText email;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

    }

    public void login(View v) {
        
        String mEmail = email.getText().toString().trim();
        String mPassword = password.getText().toString().trim();
        
        // store email, password in SharedPreferences object
        SharedPreferences.Editor e = this.getPreferences(Context.MODE_PRIVATE).edit();
        e.putString("email", mEmail);
        e.putString("password", mPassword);
        e.commit();
        
        /**
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String mEmail2 = prefs.getString("email", "");
        String mPassword2 = prefs.getString("password", "");
        
        Toast.makeText(getApplicationContext(), "Logging in... email: " + mEmail2 + ", password: " + mPassword2, Toast.LENGTH_LONG).show();
        */
        
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void cancel(View v) {
        Toast.makeText(this, "Cancelling logging in...", Toast.LENGTH_SHORT).show();
        finish();
    }
    
}
