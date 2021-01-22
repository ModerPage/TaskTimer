package me.modernpage.tasktimer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;

    public CursorRecyclerViewAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    // viewType is used when populating different kind of views on the recycler view list
    // i.e. in chat app can receive image, text, and file ...
    // viewholders would exist as much as view type
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(mCursor == null || mCursor.getCount() == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.mName.setText(R.string.instructions_heading);
            holder.mDescription.setText(R.string.instructions);
            holder.mDeleteButton.setVisibility(View.GONE);
            holder.mEditButton.setVisibility(View.GONE);
        } else {
            if(!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move to the position : " + position);
            }
            holder.mName.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)));
            holder.mDescription.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)));
            holder.mDeleteButton.setVisibility(View.VISIBLE); // TODO add onClick listener
            holder.mEditButton.setVisibility(View.VISIBLE); // TODO add onClick listener
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) || (mCursor.getCount() == 0) ? 1 : mCursor.getCount();
    }

    /**
     * Swap in a new Cursor, returning the old Cursor,
     * The returned old Cursor is <em>not</em> closed
     * @param newCursor The new Cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * if the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned
     */

    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor)
            return null;

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mDescription;
        ImageButton mEditButton;
        ImageButton mDeleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.tli_name);
            mDescription = itemView.findViewById(R.id.tli_description);
            mEditButton = itemView.findViewById(R.id.tli_edit);
            mDeleteButton = itemView.findViewById(R.id.tli_delete);
        }
    }
}
