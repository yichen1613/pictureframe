package ca.taglab.PictureFrame.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import ca.taglab.PictureFrame.R;
import ca.taglab.PictureFrame.provider.UserContentProvider;

public class AddPicture extends Activity {

    public static final String TAG = "AddPicture";

    private static final int CAMERA_REQUEST = 1;
    Uri mCapturedImageUri;

    private String imagePath;
    private EditText email;
    private EditText name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_info);
        getActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);

        Cursor cursor = getContentResolver().query(
                UserContentProvider.CONTENT_URI,
                UserTable.PROJECTION,
                null,
                null,
                null
        );

        try {
            String filename = Integer.toString(cursor.getCount() + 1) + ".jpg";
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            mCapturedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(resultCode) {
            case RESULT_OK:
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(mCapturedImageUri, projection, null, null, null);
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                imagePath = cursor.getString(column_index_data);

                // Get the image and show it in the layout
                Bitmap picture = BitmapFactory.decodeFile(imagePath);
                ((ImageView) findViewById(R.id.image)).setImageBitmap(picture);
                break;

            default:
                finish();
        }
    }

    public void save(View v) {
        ContentValues values = new ContentValues();


        values.put(UserTable.COL_NAME, name.getText().toString().trim());
        values.put(UserTable.COL_EMAIL, email.getText().toString().trim());
        values.put(UserTable.COL_IMG, imagePath);
        values.put(UserTable.COL_PASSWORD, "1234");


        getContentResolver().insert(UserContentProvider.CONTENT_URI, values);

        finish();
    }
}
