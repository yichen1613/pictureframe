package ca.taglab.PictureFrame.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import ca.taglab.PictureFrame.R;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class AddExistingPicture extends Activity {

    public static final String TAG = "AddExistingPicture";

    private static final int SELECT_IMAGE = 100;
    Uri mSelectedImageUri;

    private String imagePath;
    private EditText email;
    private EditText name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_existing_contact);
        getActionBar().hide();

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);

        Cursor cursor = getContentResolver().query(
                UserContentProvider.USER_CONTENT_URI,
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
            mSelectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImageUri);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_IMAGE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(resultCode) {
            case RESULT_OK:
                // automatically set the focus to "Name", and display the keyboard
                if (name.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }

                Uri selectedImage = intent.getData();
                String[] projection = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, projection, null, null, null);
                cursor.moveToFirst();

                int column_index_data = cursor.getColumnIndex(projection[0]);
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
        String mName = name.getText().toString().trim();
        String mEmail = email.getText().toString().toLowerCase().trim();

        if (!mName.isEmpty() && !mEmail.isEmpty()) {
            if (isValidEmail(mEmail)) {

                SharedPreferences prefs = new ObscuredSharedPreferences(this, this.getSharedPreferences("ca.taglab.PictureFrame", Context.MODE_PRIVATE));
                String ownerEmail = prefs.getString("email", "");
                int ownerId = queryForUserId(ownerEmail);
                
                if (!isExistingContact(ownerId, mEmail)) {
                    Log.d(TAG, "The contact is not an existing contact, so add it to the PFrame");
                    ContentValues values = new ContentValues();

                    values.put(UserTable.COL_NAME, mName);
                    values.put(UserTable.COL_EMAIL, mEmail);
                    values.put(UserTable.COL_IMG, imagePath);
                    values.put(UserTable.COL_PASSWORD, "1234");
                    values.put(UserTable.COL_OWNER_ID, ownerId);
                    Log.d(TAG, "Owner id is: " + ownerId + ", Owner email is: " + ownerEmail);

                    getContentResolver().insert(UserContentProvider.USER_CONTENT_URI, values);

                    finish();
                } else {
                    Toast toast = Toast.makeText(this, "A contact already exists with that email address -- please enter another one", Toast.LENGTH_LONG);
                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                    toastTV.setTextSize(30);
                    toast.show();
                }
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
        finish();
    }

    /**
     * Check if the email address is valid
     */
    public static boolean isValidEmail(String email) {
        boolean isValid = true;

        // if string has "," check validity of each email address
        if (email.contains(",")) {
            String[] emails = email.split(",");
            String currEmail;
            for (int i = 0; i < emails.length; i++) {
                currEmail = emails[i];
                try {
                    InternetAddress emailAddress = new InternetAddress(currEmail);
                    emailAddress.validate();
                } catch (AddressException ex) {
                    isValid = false;
                    break;
                }
            }
        } else {
            try {
                InternetAddress emailAddress = new InternetAddress(email);
                emailAddress.validate();
            } catch (AddressException ex) {
                isValid = false;
            }
        }
        return isValid;
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

        if (mCursor != null) {
            mCursor.close();
        }

        return uid;
    }

    /**
     * Return true if a contact belonging to the current owner already exists in the db with the given email address. 
     * Otherwise, return false.
     */
    public boolean isExistingContact(int ownerId, String email) {
        String mSelectionClause = UserTable.COL_EMAIL + "=\"" + email + "\"" + " AND " + UserTable.COL_OWNER_ID + "=" + ownerId;
        Cursor mCursor = getContentResolver().query(UserContentProvider.USER_CONTENT_URI, UserTable.PROJECTION, mSelectionClause, null, UserTable.COL_ID);

        if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() >= 1) {
            Log.d(TAG, "A contact with the email: " + email + " already exists");
            return true;
        } else {
            return false;
        }
    }
}
