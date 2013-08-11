package ca.taglab.PictureFrame.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {

    public static final String TABLE_NAME = "Message";
    public static final String COL_ID = "_id";
    public static final String COL_TO_ID = "to_id";
    public static final String COL_FROM_ID = "from_id";
    public static final String COL_DATETIME = "date";
    public static final String COL_SUBJECT = "subject";
    public static final String COL_BODY = "body";
    public static final String COL_TYPE = "type";
    public static final String COL_READ = "read";

    public static final String[] PROJECTION = {
            COL_ID,
            COL_TO_ID,
            COL_FROM_ID,
            COL_DATETIME,
            COL_BODY,
            COL_TYPE,
            COL_READ
    };
    
    private static final String CREATE_MESSAGE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TYPE + " TEXT NOT NULL, "
            + COL_DATETIME + " TEXT NOT NULL, "
            + COL_SUBJECT + " TEXT, "
            + COL_BODY + " TEXT, "
            + COL_TO_ID + " INTEGER, "
            + COL_FROM_ID + " INTEGER NOT NULL, "
            + COL_READ + " INTEGER NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(MessageTable.class.getName(), "Creating database");
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MessageTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". All existing data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
