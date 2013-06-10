package ca.taglab.PictureFrame;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import ca.taglab.PictureFrame.database.DatabaseHelper;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.database.MessageTypeTable;
import ca.taglab.PictureFrame.database.UserTable;

public class MyActivity extends Activity {
    private SQLiteDatabase data;
    /**
     * Called when the activity is first c<activity android:name=".usual.activity.Declaration" android:theme="@android:style/Theme.Translucent.NoTitleBar" />reated.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        /**
        // Testing database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

        sqlDB.delete(UserTable.TABLE_NAME, null, null);
        sqlDB.delete(MessageTable.TABLE_NAME, null, null);
        sqlDB.delete(MessageTypeTable.TABLE_NAME, null, null);

        try {
            if (sqlDB != null) {
                ContentValues values = new ContentValues();

                values.put("type", "text");
                sqlDB.insert(MessageTypeTable.TABLE_NAME, null, values);
                values.put("type", "wave");
                sqlDB.insert(MessageTypeTable.TABLE_NAME, null, values);
                values.put("type", "audio");
                sqlDB.insert(MessageTypeTable.TABLE_NAME, null, values);
                values.put("type", "video");
                sqlDB.insert(MessageTypeTable.TABLE_NAME, null, values);
                values.put("type", "picture");
                long check = sqlDB.insert(MessageTypeTable.TABLE_NAME, null, values);
                if (check != 0) {
                    Log.w("MyActivity", "Records added to MessageTypeTable successfully");
                }
                else {
                    Log.w("MyActivity", "Records were not added to MessageTypeTable");
                }
                values.clear();

                values.put("name", "Anselina");
                values.put("password", "abc");
                values.put("email", "anselina.chia@gmail.com");
                values.put("img", "none.jpg");
                check = sqlDB.insert(UserTable.TABLE_NAME, null, values);
                if (check != 0) {
                    Log.w("MyActivity", "Record added to UserTable successfully");
                }
                else {
                    Log.w("MyActivity", "Record was not added to UserTable");
                }
                values.clear();

                values.put("content", "This is a test message");
                values.put("date", "June 6, 2013");
                values.put("to_id", 1);
                values.put("from_id", 1);
                values.put("type_id", 1);
                check = sqlDB.insert(MessageTable.TABLE_NAME, null, values);
                if (check != 0) {
                    Log.w("MyActivity", "Record added to MessageTable successfully");
                }
                else {
                    Log.w("MyActivity", "Record was not added to MessageTable");
                }
                values.clear();

            }
            else {
                Log.w("MyActivity", "Connection to database failed");
            }
        }
        catch(SQLiteException ex) {
            Log.w("MyActivity", "Something is wrong");
        }
        finally {
            sqlDB.close();
        }
        **/
    }

    public void startMessageHistoryActivity(View view) {
        Intent intent = new Intent(this, MessageHistoryActivity.class);
        startActivity(intent);
    }
}