package ca.taglab.PictureFrame;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MessagesActivity extends Activity {
    private static final String TAG = "MessagesActivity";

    private ViewGroup mMessages;
    private long mOwnerId;
    private long mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getActionBar().hide();

        Bundle extras = getIntent().getExtras();
        ((TextView) findViewById(R.id.name)).setText(extras.getString("user_name"));
        mOwnerId = extras.getLong("owner_id");
        mUserId = extras.getLong("user_id");
        Log.i(TAG, "Owner ID: " + mOwnerId + ", User ID: " + mUserId);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMessages = (ViewGroup) findViewById(R.id.messages);

        new GetMessages().execute();
    }


    class GetMessages extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            return getContentResolver().query(
                    UserContentProvider.MESSAGE_CONTENT_URI,
                    MessageTable.PROJECTION,
                    MessageTable.COL_TO_ID + "=? AND " + MessageTable.COL_FROM_ID + "=?",
                    new String[] { Long.toString(mOwnerId), Long.toString(mUserId) },
                    MessageTable.COL_DATETIME
            );
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            String body, type;

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    type = cursor.getString(cursor.getColumnIndex(MessageTable.COL_TYPE));
                    body = cursor.getString(cursor.getColumnIndex(MessageTable.COL_BODY));
                    addMessage(type, body);
                    cursor.moveToNext();
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * Add message to the user interface.
     * @param type  Type of message (i.e. text, photo, video)
     * @param body  Body of the message
     */
    private void addMessage(String type, String body) {
        Log.i(TAG, "Type: " + type);
        Log.i(TAG, body);

        final ViewGroup newView = (ViewGroup) LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.message_item, mMessages, false);

        if (type.equals("text")) {
            newView.findViewById(R.id.text).setVisibility(View.VISIBLE);
            ((TextView) newView.findViewById(R.id.text)).setText(body);
        }

        if (type.equals("image")) {
            newView.findViewById(R.id.photo).setVisibility(View.VISIBLE);
            ((ImageView) newView.findViewById(R.id.photo)).setImageBitmap(getImage(new File(body), 1000));
        }

        mMessages.addView(newView, 0);
    }


    public static Bitmap getImage(File f, int max_size) {
        //File f = new File(Environment.getExternalStorageDirectory() + "/VocabNomad", path);
        Bitmap b = null;

        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > max_size || o.outWidth > max_size) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(max_size /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
}
