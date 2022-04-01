package ir.mahdiparastesh.notepad.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import ir.mahdiparastesh.notepad.R;
import ir.mahdiparastesh.notepad.dialog.BackButtonDialogFragment;
import ir.mahdiparastesh.notepad.dialog.DeleteDialogFragment;
import ir.mahdiparastesh.notepad.dialog.SaveButtonDialogFragment;
import ir.mahdiparastesh.notepad.fragment.NoteEditFragment;
import ir.mahdiparastesh.notepad.utils.ThemeManager;

public class NoteEditActivity extends AppCompatActivity implements
        BackButtonDialogFragment.Listener,
        DeleteDialogFragment.Listener,
        SaveButtonDialogFragment.Listener,
        NoteEditFragment.Listener {

    String external;

    @Override
    public boolean isShareIntent() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        getSupportActionBar().setElevation(getResources().getDimensionPixelSize(R.dimen.action_bar_elevation));

        LinearLayout noteViewEdit = findViewById(R.id.noteViewEdit);

        ThemeManager.setBackgroundColor(this, noteViewEdit);

        if (!(getSupportFragmentManager().findFragmentById(R.id.noteViewEdit) instanceof NoteEditFragment)) {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            boolean supportedFile = type != null &&
                    (type.startsWith("text/") || type.startsWith("application/"));// || type.equals("")

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (supportedFile) {
                    external = getExternalContent();
                    if (external != null) newNote();
                    else {
                        showToast(R.string.loading_external_file);
                        finish();
                    }
                } else {
                    showToast(R.string.loading_external_file);
                    finish();
                }

                // Intent sent through Google Now "note to self"
            } else if ("com.google.android.gm.action.AUTO_SEND".equals(action) && type != null) {
                if (supportedFile) {
                    external = getExternalContent();
                    if (external != null) try {
                        FileOutputStream output = openFileOutput(String.valueOf(System.currentTimeMillis()), Context.MODE_PRIVATE);
                        output.write(external.getBytes());
                        output.close();

                        showToast(R.string.note_saved);
                        finish();
                    } catch (IOException e) {
                        showToast(R.string.failed_to_save);
                        finish();
                    }
                }
            } else if (Intent.ACTION_EDIT.equals(action) && supportedFile) {
                external = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (external != null) {
                    newNote();
                    return;
                }
                finish();
            } else if (Intent.ACTION_VIEW.equals(action) && supportedFile) {
                try {
                    InputStream in = getContentResolver().openInputStream(intent.getData());
                    Reader rd = new InputStreamReader(in, StandardCharsets.UTF_8);
                    char[] buffer = new char[4096];
                    int len;
                    StringBuilder sb = new StringBuilder();
                    while ((len = rd.read(buffer)) != -1)
                        sb.append(buffer, 0, len);
                    rd.close();
                    in.close();
                    external = sb.toString();
                } catch (Exception ignored) {
                }
                if (external != null) {
                    newNote();
                    return;
                }
                finish();
            } else newNote();
        }
    }

    private String getExternalContent() {
        String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (text == null) return null;

        String subject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        if (subject == null) return text;

        return subject + "\n\n" + text;
    }

    private void newNote() {
        Bundle bundle = new Bundle();
        bundle.putString("filename", "new");

        Fragment fragment = new NoteEditFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.noteViewEdit, fragment, "NoteEditFragment")
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (external != null) {
            EditText noteContents = findViewById(R.id.editText1);
            noteContents.setText(external);
            noteContents.setSelection(external.length(), external.length());
            external = null;
        }
    }

    // Keyboard shortcuts
    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.isCtrlPressed()) {
            NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
            fragment.dispatchKeyShortcutEvent(event.getKeyCode());
            return true;
        }
        return super.dispatchKeyShortcutEvent(event);
    }

    @Override
    public void onBackPressed() {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onBackPressed(null);
    }

    @Override
    public void onBackDialogNegativeClick(String filename) {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onBackDialogNegativeClick(null);
    }

    @Override
    public void onBackDialogPositiveClick(String filename) {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onBackDialogPositiveClick(null);
    }

    @Override
    public void onDeleteDialogPositiveClick() {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onDeleteDialogPositiveClick();
    }

    @Override
    public void onSaveDialogNegativeClick() {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onSaveDialogNegativeClick();
    }

    @Override
    public void onSaveDialogPositiveClick() {
        NoteEditFragment fragment = (NoteEditFragment) getSupportFragmentManager().findFragmentByTag("NoteEditFragment");
        fragment.onSaveDialogPositiveClick();
    }

    @Override
    public void showBackButtonDialog(String filename) {
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);

        DialogFragment backFragment = new BackButtonDialogFragment();
        backFragment.setArguments(bundle);
        backFragment.show(getSupportFragmentManager(), "back");
    }

    @Override
    public void showDeleteDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_title", R.string.dialog_delete_button_title);

        DialogFragment deleteFragment = new DeleteDialogFragment();
        deleteFragment.setArguments(bundle);
        deleteFragment.show(getSupportFragmentManager(), "delete");
    }

    @Override
    public void showSaveButtonDialog() {
        DialogFragment saveFragment = new SaveButtonDialogFragment();
        saveFragment.show(getSupportFragmentManager(), "save");
    }

    private void showToast(int message) {
        Toast toast = Toast.makeText(this, getResources().getString(message), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public String loadNote(String filename) {
        return null;
    }

    @Override
    public String loadNoteTitle(String filename) {
        return null;
    }

    @Override
    public void exportNote(String filename) {
    }

    @Override
    public void printNote(String contentToPrint) {
    }
}
