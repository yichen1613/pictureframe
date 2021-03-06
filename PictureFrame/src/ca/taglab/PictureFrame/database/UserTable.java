package ca.taglab.PictureFrame.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserTable {

    // Database table
    public static final String TABLE_NAME = "User";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EMAIL = "email";
    public static final String COL_IMG = "img";
    public static final String COL_OWNER_ID = "owner_id";

    public static final String[] PROJECTION = {
            COL_ID,
            COL_NAME,
            COL_EMAIL,
            COL_IMG,
            COL_OWNER_ID
    };

    // SQL statement for database creation
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_PASSWORD + " TEXT, "
            + COL_EMAIL + " TEXT NOT NULL, "
            + COL_IMG + " TEXT NOT NULL, "
            + COL_OWNER_ID + " INTEGER NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(UserTable.class.getName(), "Creating database");
        db.execSQL(CREATE_USER_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". All existing data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
