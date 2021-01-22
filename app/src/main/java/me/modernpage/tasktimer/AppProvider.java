package me.modernpage.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;

/**
 * Provider for the TaskTimer app. This is the only that knows about {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    /**
     * Utility class to aid in matching URIs in content providers.
     *
     * Developers usually create content URIs from the authority by appending paths that point to individual tables.
     * For example, if you have two tables table1 and table2, you combine the authority from the previous example
     * to yield the content URIs com.example.<appname>.provider/table1 and com.example.<appname>.provider/table2.
     * Paths aren't limited to a single segment, and there doesn't have to be a table for each level of the path.
     *
     * We use uri matcher to provide url for different table path
     */
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    /**
     * Authority is a symbolic name of the content provider, it must be unique.
     *
     * if your Android package name is com.example.<appname>,
     * you should give your provider the authority com.example.<appname>.provider.
     */
    static final String CONTENT_AUTHORITY = "me.modernpage.tasktimer.AppProvider";

    /**
     * A content URI is a URI that identifies data in a provider.
     * Content URIs include the symbolic name of the entire provider (its authority)
     * and a name that points to a table or file (a path).
     *
     * other apps that wants to access provider use this uri, so we make it public
     */
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    /*
    private static final int TASK_TIMINGS = 300;
    private static final int TASK_TIMINGS_ID = 301;
     */

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // eg. content://com.timbuchalka.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        // eg. content://com.timbuchalka.tasktimer.provider/Tasks/8
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TASK_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: called");
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    /**
     * This content provider's query method calls the query builders query method passing to the database object that we get from {@link AppDatabase}(mOpenHelper)
     *
      */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = mUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {

            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

                case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

//            case TIMINGS:
//                queryBuilder.setTables(TimingContract.TABLE_NAME);
//                break;
//
//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingContract.TABLE_NAME);
//                long timingId = TimingsContract.getTimingId(uri);
//                queryBuilder.appendWhere(TimingsContract.Colums._ID + " = " + timingId);
//                break;
//
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsCotract.TABLE_NAME);
//                break;
//
//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsCotract.TABLE_NAME);
//                long timingId = DurationsCotract.getTimingId(uri);
//                queryBuilder.appendWhere(DurationsCotract.Colums._ID + " = " + timingId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db,projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the given URI.
     * The returned MIME type should start with vnd.android.cursor.item for a single record,
     * or vnd.android.cursor.dir/ for multiple items, such as a whole table. These are custom mime types definition
     * This method can be called from multiple threads, as described in Processes and Threads.
     *
     * Note that there are no permissions needed for an application to access this information;
     * if your content provider requires read and/or write permissions, or is not exported,
     * all applications can still call this method regardless of their access permissions.
     * This allows them to retrieve the MIME type for a URI when dispatching intents.
     *
     * one way that these types might be used is when displaying our data using intents
     * so output will include an activity to display reports and we could allow that
     * activity to be launched by another app using an intent so in that case the
     * other app would pass the appropriate content type in the intent so that would
     * know whether it was asking is to display the entire table or just a specific row
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {

            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

//            case TIMINGS:
//               return TimingsContract.Timings.CONTENT_TYPE;
//
//            case TIMINGS_ID:
//               return TimingsContract.Timings.CONTENT_ITEM_TYPE;
//
//            case TASK_DURATIONS:
//               return DurationsContract.TaskDurations.CONTENT_TYPE;
//
//            case TASK_DURATIONS_ID:
//               return DurationsContract.TaskDurations.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "Enter insert, called with uri: " + uri);
        final int match = mUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase database;

        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                recordId = database.insert(TasksContract.TABLE_NAME, null, contentValues);
                if(recordId >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
//            case TIMINGS:
//                database = mOpenHelper.getWritableDatabase();
//                recordId = database.insert(TimingsContract.TABLE_NAME, null, contentValues);
//                if(recordId >= 0) {
//                    returnUri = TimingsContract.Timings.buildTimingUri(recordId);
//                } else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Existing insert, returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete called with uri: " + uri);
        final int match = mUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase database;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                count = database.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                database = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;
                if(selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                database = mOpenHelper.getWritableDatabase();
//                count = database.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                database = mOpenHelper.getWritableDatabase();
//                long taskId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + taskId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = database.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Existing update, returning " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri: " + uri);
        final int match = mUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase database;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                count = database.update(TasksContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TASKS_ID:
                database = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;
                if(selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.update(TasksContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                database = mOpenHelper.getWritableDatabase();
//                count = database.update(TimingsContract.TABLE_NAME, contentValues, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                database = mOpenHelper.getWritableDatabase();
//                long taskId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + taskId;
//                if(selection != null && selection.length() > 0) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = database.update(TimingsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Existing update, returning " + count);
        return count;
    }
}
