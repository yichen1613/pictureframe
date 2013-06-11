package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ca.taglab.PictureFrame.database.DatabaseHelper;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.email.MailSenderActivity;
import ca.taglab.PictureFrame.provider.UserContentProvider;

public class MyActivity extends Activity {
    DatabaseHelper db;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_NAME, "Steve");
        values.put(UserTable.COL_EMAIL, "steve@taglab.ca");
        values.put(UserTable.COL_IMG, "steve.jpg");
        values.put(UserTable.COL_PASSWORD, "5t3v3");
        getContentResolver().insert(UserContentProvider.CONTENT_URI, values);

        db = new DatabaseHelper(getApplicationContext());

    }

    public void addImage(View v) {
        // Open activity
        startActivity(new Intent(this, ScreenSlideActivity.class));
    }

    public void startMessageHistoryActivity(View view) {
        Intent intent = new Intent(this, MessageHistoryActivity.class);
        startActivity(intent);
    }

    public void startMailSenderActivity(View view) {
        Intent intent = new Intent(this, MailSenderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        db.close();
    }
}