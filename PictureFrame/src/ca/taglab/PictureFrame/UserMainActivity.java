package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import ca.taglab.PictureFrame.database.AddExistingPicture;
import ca.taglab.PictureFrame.database.AddPicture;
import ca.taglab.PictureFrame.database.UserTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;

public class UserMainActivity extends Activity {

    private View mContact;
    private View mContactImage;
    private View mContactNew;
    private View mContactExisting;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_menu);

        getActionBar().hide();

        mContact = findViewById(R.id.add_contact);
        mContactImage = findViewById(R.id.contact);
        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactImage.setVisibility(View.GONE);
                mContactNew.setVisibility(View.VISIBLE);
                mContactExisting.setVisibility(View.VISIBLE);
            }
        });

        mContactNew = findViewById(R.id.contact_new);
        mContactNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddPicture.class));
            }
        });
        
        mContactExisting = findViewById(R.id.contact_existing);
        mContactExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddExistingPicture.class));
            }
        });
        
        
        findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, ScreenSlideActivity.class));
                
            }
        });
         

        /**
        findViewById(R.id.add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMainActivity.this, AddPicture.class));
            }
        });
         */
    }
}
