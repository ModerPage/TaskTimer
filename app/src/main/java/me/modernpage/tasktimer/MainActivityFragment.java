package me.modernpage.tasktimer;


import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;


/**
 * A simple {@link Fragment} subclass.
 */
//    The Loader API lets you load data from a content provider or other data source for display in an FragmentActivity or Fragment.

//    If you fetch the data directly in the activity or fragment, your users will suffer from lack of responsiveness due to performing potentially slow queries from the UI thread.
//    If you fetch the data from another thread, perhaps with AsyncTask, then you're responsible for managing both the thread and the UI thread through various activity or
//    fragment lifecycle events, such as onDestroy() and configurations changes.

//    cursor loader just runs query for us on the background thread

//    An abstract class associated with an FragmentActivity or Fragment for managing one or more Loader instances.
//    There is only one LoaderManager per activity or fragment, but a LoaderManager can manage multiple loaders.

//    To get LoaderManager, call getSupportLoaderManager() from the activity or fragment.

//    LoaderManager.LoaderCallbacks	This interface contains callback methods that are called when loader events occur.
//    The interface defines three callback methods:

//    This interface is typically implemented by your activity or fragment and is registered when you call initLoader() or restartLoader().

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";
    private CursorRecyclerViewAdapter mCursorRecyclerViewAdapter;
    private RecyclerView mRecyclerView;

    public static final int LOADER_ID = 0;
    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: called");
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = view.findViewById(R.id.task_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCursorRecyclerViewAdapter = new CursorRecyclerViewAdapter(null);
        mRecyclerView.setAdapter(mCursorRecyclerViewAdapter);
        return view;
    }

//    on fragment it is best to call getting the loader on ActivityCreated
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: called");
        super.onActivityCreated(savedInstanceState);
        // tell the manager which loader is initializing
        // the second argument is always passed as null when using android cursor loader,
        // because it does not expect any other arguments
        // the third argument tells which object will be handling loader manager callback
        LoaderManager.getInstance(this).initLoader(LOADER_ID,null, this);
    }

//    Instantiate and return a new Loader for the given ID.
//    When you attempt to access a loader (for example, through initLoader()), it checks to see whether the loader specified by the ID exists.
//    If it doesn't, it triggers the LoaderManager.LoaderCallbacks method onCreateLoader(). This is where you create a new loader.
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with the id: " + id);
        // general use case for more than one cursor loader

        // columns is to fetch from database
        String[] projection = {TasksContract.Columns._ID,
                            TasksContract.Columns.TASKS_NAME,
                            TasksContract.Columns.TASKS_DESCRIPTION,
                            TasksContract.Columns.TASKS_SORTORDER};

        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + ", " + TasksContract.Columns.TASKS_NAME;

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),TasksContract.CONTENT_URI,projection,null,null,sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

//    When the cursor loader retrieved all the data on a background thread, lets the loader manager know and loader manager calls onLoadFinished
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//    we use cursor returned in an adapter that recycler view can use to display adapter
        Log.d(TAG, "onLoadFinished: called");
        mCursorRecyclerViewAdapter.swapCursor(data);
        int count = mCursorRecyclerViewAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);
        // here we don't close the cursor, it not the cursor that belongs to cursor loader,
        // the cursor loader won't get notification if the cursor is being closed
    }

    
    // called when activity or fragment is stopped
    // called when a previously created loader is being reset (when you call destroyLoader(int) or when the activity or fragment is destroyed , 
    // and thus making its data unavailable. Your code should remove any references it has to the loader's data.
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: called");
        mCursorRecyclerViewAdapter.swapCursor(null);
    }
}
