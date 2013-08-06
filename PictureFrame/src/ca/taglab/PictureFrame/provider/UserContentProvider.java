package ca.taglab.PictureFrame.provider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ca.taglab.PictureFrame.database.DatabaseHelper;
import ca.taglab.PictureFrame.database.MessageTable;
import ca.taglab.PictureFrame.database.UserTable;

public class UserContentProvider extends ContentProvider {
    
    private DatabaseHelper dbHelper;

    // Used for the UriMatcher
    private static final int USERS = 1;
    private static final int USER_ID = 2;
    
    private static final int MESSAGES = 3;

    public static final String AUTHORITY = "ca.taglab.PictureFrame.provider";

    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");
    public static final Uri MESSAGE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");

    public static final String USER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/users";
    public static final String USER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/users";
    public static final String MESSAGE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/messages";

    // Construct a URI matcher to detect URIs
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "users", USERS);
        uriMatcher.addURI(AUTHORITY, "users/#", USER_ID);
        
        uriMatcher.addURI(AUTHORITY, "users/#/messages/#", MESSAGES);
    }

    /**
     * Create a new instance of the content provider.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Get the MIME type of the specified URI.
     *
     * @param uri	The URI of the object
     * @return		The MIME type of the object
     */
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            
            case USERS:
                return USER_CONTENT_TYPE;
            
            case USER_ID:
                return USER_CONTENT_ITEM_TYPE;
            
            case MESSAGES:
                return MESSAGE_CONTENT_TYPE;
            
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    /**
     * Query objects in the database.
     *
     * @param uri			The URI of the object
     * @param projection	A list of columns to be queried
     * @param selection		The where clause
     * @param selectionArgs	The arguments for the where clause
     * @param sortOrder		The order of elements
     *
     * @return				A cursor containing the queried objects
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        
        switch (uriMatcher.match(uri)) {
            
            //////////////////////////////////////////////////////
            //						USERS						//
            //////////////////////////////////////////////////////
            
            case USERS:
                break;
            
            case USER_ID:
            /*
			 * Query a specific user from the User table.
			 * Path: //users/#
			 */
                queryBuilder.setTables(UserTable.TABLE_NAME);
                String uid = uri.getLastPathSegment();
                queryBuilder.appendWhere(UserTable.COL_ID + "=" + uid);
                break;

            
            //////////////////////////////////////////////////////
            //						MESSAGES					//
            //////////////////////////////////////////////////////
            
            case MESSAGES:
            /*
			 * Query the messages sent from from_id to to_id in the Message table.
			 * Path: //users/#/messages/#
			 */
                queryBuilder.setTables(MessageTable.TABLE_NAME);
                String to_id = uri.getPathSegments().get(1);
                String from_id = uri.getPathSegments().get(3);
                queryBuilder.appendWhere(MessageTable.COL_TO_ID + "=" + to_id);
                queryBuilder.appendWhere(MessageTable.COL_FROM_ID + "=" + from_id);
                break;
            
            
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
        
    }

    
    /**
     * Insert a new object into the database.
     *
     * @param uri	    The URI of the object to insert
     * @param values    The content values containing the object's information
     *
     * @throws android.database.SQLException    If an error occurred while inserting the object
     *
     * @return			The URI of the newly inserted object
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        String table;
        long row_id = 0;
        
        switch (uriMatcher.match(uri)) {
        
            case USER_ID:
            case USERS:
                table = UserTable.TABLE_NAME;
                break;
        
            case MESSAGES:
                table = MessageTable.TABLE_NAME;
                break;
            
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Insert entry into table
        row_id = sqlDB.insert(table, null, values);
        
        Uri new_uri = ContentUris.withAppendedId(uri, row_id);
        getContext().getContentResolver().notifyChange(new_uri, null);
        return new_uri;
    }

    
    /**
     * Delete an object from the database.
     *
     * @param uri		    The URI of the object to delete
     * @param selection		The where clause
     * @param selectionArgs	The where arguments
     *
     * @return			    The number of rows that have been deleted
     */
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
                } else {
                    rowsDeleted = sqlDB.delete(UserTable.TABLE_NAME, UserTable.COL_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Update an object in the database.
     *
     * @param uri		    The URI of the object to update
     * @param values	    The content values containing the updated information
     * @param selection		The where clause
     * @param selectionArgs	The arguments for the where clause
     *
     * @throws IllegalArgumentException when the URI is not recognized
     *
     * @return			    The number of affected rows
     */
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


    /**
     * Add an equality constraint to the where clause.
     *
     * @param where	Existing where clause
     * @param col	Column name
     * @param val	Value
     *
     * @return		The new where clause
     */
    private String ADD_CONSTRAINT(String where, String col, Object val) {
        return col + "=" + val + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
    }

}
