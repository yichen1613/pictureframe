package ca.taglab.PictureFrame;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.email.ReadEmailAsyncTask;
import ca.taglab.PictureFrame.provider.UserContentProvider;

import java.util.Timer;
import java.util.TimerTask;

public class ReceiveMailActivity extends Activity {

    private final static String TAG = "ReceiveMailActivity";
    private final static int REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_mail);
        getActionBar().hide();
        
    }
    
    public void retrieveMail(View v) {
        new ReadEmailAsyncTask(this, "ALL").execute();
    }
    
    public void refreshMail(View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getUnreadEmails();
            }
        }, 0, REFRESH_INTERVAL);
    }
    
    public void readMail(View v) {
        Cursor mCursor = getContentResolver().query(UserContentProvider.MESSAGE_CONTENT_URI, MessageTable.PROJECTION, null, null, MessageTable.COL_ID);
        
        if (mCursor != null) {
            Log.d(TAG, "Number of entries in MessageTable: " + mCursor.getCount());

            while (mCursor.moveToNext()) {
                int mId = mCursor.getInt(mCursor.getColumnIndex(MessageTable.COL_ID));
                int mToId = mCursor.getInt(mCursor.getColumnIndex(MessageTable.COL_TO_ID));
                int mFromId = mCursor.getInt(mCursor.getColumnIndex(MessageTable.COL_FROM_ID));
                String mDatetime = mCursor.getString(mCursor.getColumnIndex(MessageTable.COL_DATETIME));
                String mType = mCursor.getString(mCursor.getColumnIndex(MessageTable.COL_TYPE));
                String mBody = mCursor.getString(mCursor.getColumnIndex(MessageTable.COL_BODY));

                String msgContents = "==============Message " + mId + "=============="
                        + "\nFrom ID: " + mFromId
                        + "\nTo ID: " + mToId
                        + "\nDate: " + mDatetime
                        + "\nType: " + mType
                        + "\nBody: \n\n" + mBody;
                Toast toast = Toast.makeText(this, msgContents, Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                toastTV.setTextSize(22);
                toast.show();
            }

        } else {
            Log.d(TAG, "No entries found in MessageTable");
        }

        if (mCursor != null) {
            mCursor.close();
        }
    }
    
    public void cancel(View v) {
        finish();
    }

    public void getUnreadEmails() {
        new ReadEmailAsyncTask(this, "UNREAD").execute();
    }
}
