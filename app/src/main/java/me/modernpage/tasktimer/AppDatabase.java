package me.modernpage.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A helper class to manage database creation and version management.
 * The only class that should use this is {@link AppProvider}.
 *
 * This class makes it easy for ContentProvider implementations to defer opening and upgrading the database until first use,
 * to avoid blocking application startup with long-running database upgrades.
 */
public class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

    // Implement AppDatabase as a Singleton

    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object in a thread-safe manner by lazy initialization
     * @param context
     * @return
     */
    static AppDatabase getInstance(Context context) {
        Log.d(TAG, "getInstance: starts");
        if(instance == null) {
            synchronized (AppDatabase.class) {
                if(instance == null) {
                    Log.d(TAG, "getInstance: creating new instance");
                    instance = new AppDatabase(context);
                }
            }
        }
        Log.d(TAG, "getInstance: ends");
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: starts");

        String sSQL;

        sSQL = "create table " + TasksContract.TABLE_NAME + " ("
                + TasksContract.Columns._ID + " integer primary key not null, "
                + TasksContract.Columns.TASKS_NAME + " text not null, "
                + TasksContract.Columns.TASKS_DESCRIPTION + " text, "
                + TasksContract.Columns.TASKS_SORTORDER + " integer);";
        Log.d(TAG, "onCreate: sSQL: " + sSQL);

        sqLiteDatabase.execSQL(sSQL);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");

        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    @Override
    public synchronized void close() {
        Log.d(TAG, "close: called");
        instance.close();
    }
}
