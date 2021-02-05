package me.modernpage.tasktimer;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class AddEditActivity extends AppCompatActivity implements
        AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "AddEditActivity";
    private static final int DIALOG_ID_CANCEL_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = new AddEditActivityFragment();
        fragment.setArguments(arguments);
        fragmentManager.beginTransaction().replace(R.id.main_addedit_fragment_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment)
                        getSupportFragmentManager().findFragmentById(R.id.main_addedit_fragment_container);
                if(fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConfirmationDialog() {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onSaveClicked() {
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.main_addedit_fragment_container);
        if(fragment.canClose()) {
            super.onBackPressed();
        } else {
            // show dialogue to get confirmation to quit editing
            showConfirmationDialog();
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }
}
