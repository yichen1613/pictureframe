package ca.taglab.PictureFrame;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.provider.UserContentProvider;

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

        mMessages.addView(newView, 0);
    }
}
