package ca.taglab.PictureFrame.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {

    public static final String TABLE_NAME = "Message";
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_DATE = "date";
    public static final String COL_TO_ID = "to_id";
    public static final String COL_FROM_ID = "from_id";
    public static final String COL_TYPE_ID = "type_id";

    private static final String CREATE_MESSAGE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CONTENT + " TEXT NOT NULL, "
            + COL_DATE + " TEXT NOT NULL, "
            + COL_TO_ID + " INTEGER NOT NULL, "
            + COL_FROM_ID + " INTEGER NOT NULL, "
            + COL_TYPE_ID + " INTEGER NOT NULL"
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
