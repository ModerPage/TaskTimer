package me.modernpage.tasktimer;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
                                                                AddEditActivityFragment.OnSaveClicked,
                                                                AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    private static final int DIALOG_ID_DELETE = 1;
    private static final int DIALOG_ID_CANCEL_EDIT = 2;

    // Whether or not the activity is in 2-pane mode
    // i.e. running in landscape on a tablet
    private boolean mTwoPane = false;

    private AlertDialog mAlertDialog;           // module scope because we need to dismiss it in onStop
                                                // e.g. When orientation changes to avoid memory leaks.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // checking whether app is running on landscape mode or over 600dp screen size
        if (findViewById(R.id.addedit_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_showDurations:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showAboutDialog() {
        View messageView = LayoutInflater.from(this).inflate(R.layout.layout_aboud_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mAlertDialog != null && mAlertDialog.isShowing())
                    mAlertDialog.dismiss();
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(true);

        // this way won't work for us, since icon and title is not part of the view
        // when tap on them, dismissing won't work
        // second problem is that links are clickable , that intercepts taps on the entire line of text
//        messageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: entering messageView Onclick, showing = " + mAlertDialog.isShowing());
//                if(mAlertDialog != null && mAlertDialog.isShowing())
//                    mAlertDialog.dismiss();
//            }
//        });
        TextView tv = messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);
        mAlertDialog.show();
    }

    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: called");
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldaig_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("TaskId", task.getId());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");

        if (mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

            FragmentManager fragmentManager = getSupportFragmentManager();
//            using replace() instead of add(), removing all existing fragments then add, but add() always adding
            fragmentManager.beginTransaction()
                    .replace(R.id.addedit_container, fragment)
                    .commit();
        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            // in a single-pane mode, start the detail activity for the selected item id
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) { // editing a task
                detailIntent.putExtra(Task.class.getSimpleName(), task);
                startActivity(detailIntent);
            } else // adding a new task
                startActivity(detailIntent);
        }
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: called");

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.addedit_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                if(BuildConfig.DEBUG && taskId == 0) throw new AssertionError("task id is zero");
                getContentResolver().delete(TasksContract.CONTENT_URI, TasksContract.Columns._ID + " = ?", new String[]{String.valueOf(taskId)});
                break;
            case DIALOG_ID_CANCEL_EDIT:
                // no action required, just continue editing
                break;
        }

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                // no action required, no deleting task
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.addedit_container);
        if(fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            // show dialogue to get confirmation to quit editing
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

            dialog.setArguments(args);
            dialog.show(fragmentManager, null);
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
        if(mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }
}
