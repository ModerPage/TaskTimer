package me.modernpage.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static me.modernpage.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static me.modernpage.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * A contract class is a public final class that contains constant definitions for the URIs,
 * column names, MIME types, and other meta-data about the ContentProvider.
 * It can also contain static helper methods to manipulate the URIs. In simple terms,
 * Contract class is used by the developers to define a schema and have a convention where to find the database constants.
 */

public class TasksContract {
    static final String TABLE_NAME = "Tasks";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "name";
        public static final String TASKS_DESCRIPTION = "description";
        public static final String TASKS_SORTORDER = "sortOrder";

        private Columns(){
            // prevent instantiation
        }
    }

    /**
     * The URI to access the Tasks table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    /**
     * the reason to use ContentURis instead of Uri, because we are appending the content part of uri, not basic uri
     * @param taskId
     * @return id for the table of the uri
     */
    static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI,taskId);
    }

    static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
