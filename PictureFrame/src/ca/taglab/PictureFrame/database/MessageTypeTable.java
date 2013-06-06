package ca.taglab.PictureFrame.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTypeTable {

    public static final String TABLE_NAME = "MessageType";
    public static final String COL_ID = "_id";
    public static final String COL_TYPE = "type";

    private static final String CREATE_MESSAGETYPE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TYPE + " TEXT UNIQUE NOT NULL"
                    + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(MessageTypeTable.class.getName(), "Creating database");
        db.execSQL(CREATE_MESSAGETYPE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MessageTypeTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". All existing data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
