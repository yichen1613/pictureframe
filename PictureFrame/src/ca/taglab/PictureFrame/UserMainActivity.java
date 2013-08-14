package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import ca.taglab.PictureFrame.database.AddPicture;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;

public class UserMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_menu);

        getActionBar().hide();

        findViewById(R.id.add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, ScreenSlideActivity.class));
            }
        });

        findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddPicture.class));
            }
        });
    }
}
