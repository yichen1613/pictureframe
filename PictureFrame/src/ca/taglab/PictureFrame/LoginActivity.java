package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
        SharedPreferences.Editor e = this.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE).edit();
        e.putString("email", mEmail);
        e.putString("password", mPassword);
        e.commit();
        
        finish();
    }

    public void cancel(View v) {
        finish();
    }
    
}
