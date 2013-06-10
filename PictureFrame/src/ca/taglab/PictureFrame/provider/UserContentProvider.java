package ca.taglab.PictureFrame.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ca.taglab.PictureFrame.database.DatabaseHelper;
import ca.taglab.PictureFrame.database.UserTable;

public class UserContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper;

    // Used for the UriMatcher
    private static final int USERS = 1;
    private static final int USER_ID = 2;

    public static final String AUTHORITY = "ca.taglab.PictureFrame.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");

    // MIME type providing all users, a single user
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/users";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/users";

    // Construct a URI matcher to detect URIs referencing all users, a single user
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "users", USERS);
        uriMatcher.addURI(AUTHORITY, "users/#", USER_ID);
    }

    @Override
    // System calls onCreate() when it starts up the provider
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    // Return the MIME type corresponding to a content URI
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USERS:
                return CONTENT_TYPE;
            case USER_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Specify the User table to work with
        queryBuilder.setTables(UserTable.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case USERS:
                // do nothing
                break;
            case USER_ID:
                String uid = uri.getLastPathSegment();
                queryBuilder.appendWhere(UserTable.COL_ID + "=" + uid);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        long row_id = 0;
        switch (uriMatcher.match(uri)) {
            case USERS:
                row_id = sqlDB.insert(UserTable.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse("users/" + row_id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)) {
            case USERS:
                rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, UserTable.COL_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, UserTable.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriMatcher.match(uri)) {
            case USERS:
                rowsUpdated = sqlDB.update(UserTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(UserTable.TABLE_NAME, values, UserTable.COL_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(UserTable.TABLE_NAME, values, UserTable.COL_ID + "=" + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}
