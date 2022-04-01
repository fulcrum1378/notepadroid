package ir.mahdiparastesh.notepad.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;

import ir.mahdiparastesh.notepad.R;
import ir.mahdiparastesh.notepad.fragment.dialog.FirstViewDialogFragment;
import ir.mahdiparastesh.notepad.utils.ThemeManager;
import us.feras.mdv.MarkdownView;

public class NoteViewFragment extends Fragment {

    private MarkdownView markdownView;

    String filename = "";
    String contentsOnLoad = "";
    int firstLoad;
    boolean showMessage = true;

    IntentFilter filter = new IntentFilter("ir.mahdiparastesh.notepad.DELETE_NOTES");

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement Listener");
        }
    }
    DeleteNotesReceiver receiver = new DeleteNotesReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences pref = getActivity().getSharedPreferences(getActivity().getPackageName() + "_preferences", Context.MODE_PRIVATE);
        return inflater.inflate(
                pref.getBoolean("markdown", false)
                        ? R.layout.fragment_note_view_md
                        : R.layout.fragment_note_view, container, false);
    }

    Listener listener;

    @SuppressLint("ClickableViewAccessibility")
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        filename = getArguments().getString("filename");
        String title;
        try {
            title = listener.loadNoteTitle(filename);
        } catch (IOException e) {
            title = getResources().getString(R.string.view_note);
        }

        getActivity().setTitle(title);

        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.ic_recents_logo)).getBitmap();

        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(title, bitmap, ContextCompat.getColor(getActivity(), R.color.primary));
        getActivity().setTaskDescription(taskDescription);

        // Show the Up button in the action bar.
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Animate elevation change
        if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-large")) {
            LinearLayout noteViewEdit = getActivity().findViewById(R.id.noteViewEdit);
            LinearLayout noteList = getActivity().findViewById(R.id.noteList);

            noteList.animate().z(0f);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                noteViewEdit.animate().z(getResources().getDimensionPixelSize(R.dimen.note_view_edit_elevation_land));
            else
                noteViewEdit.animate().z(getResources().getDimensionPixelSize(R.dimen.note_view_edit_elevation));
        }

        TextView noteContents = getActivity().findViewById(R.id.textView);
        markdownView = getActivity().findViewById(R.id.markdownView);

        String css = ThemeManager.applyNoteViewTheme(getActivity(), noteContents, markdownView);

        try {
            contentsOnLoad = listener.loadNote(filename);
        } catch (IOException e) {
            showToast(R.string.error_loading_note);

            // Add NoteListFragment or WelcomeFragment
            Fragment fragment;
            if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-normal"))
                fragment = new NoteListFragment();
            else
                fragment = new WelcomeFragment();

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.noteViewEdit, fragment, "NoteListFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();
        }

        if (noteContents != null) noteContents.setText(contentsOnLoad);

        if (markdownView != null)
            markdownView.loadMarkdown(contentsOnLoad, "data:text/css;base64," +
                    Base64.encodeToString(css.getBytes(), Base64.DEFAULT));

        // Show a toast message if this is the user's first time viewing a note
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        firstLoad = sharedPref.getInt("first-load", 0);
        if (firstLoad == 0) {
            // Show dialog with info
            DialogFragment firstLoad = new FirstViewDialogFragment();
            firstLoad.show(getActivity().getSupportFragmentManager(), "firstloadfragment");

            // Set first-load preference to 1; we don't need to show the dialog anymore
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("first-load", 1);
            editor.apply();
        }

        // Detect single and double-taps using GestureDetector
        final GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });

        detector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (sharedPref.getBoolean("show_double_tap_message", true)) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("show_double_tap_message", false);
                    editor.apply();
                }

                Bundle bundle = new Bundle();
                bundle.putString("filename", filename);

                Fragment fragment = new NoteEditFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                        .commit();

                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (sharedPref.getBoolean("show_double_tap_message", true) && showMessage) {
                    showToastLong(R.string.double_tap);
                    showMessage = false;
                }

                return false;
            }

        });

        if (noteContents != null) noteContents.setOnTouchListener((v, event) -> {
            detector.onTouchEvent(event);
            return false;
        });

        if (markdownView != null) markdownView.setOnTouchListener((v, event) -> {
            detector.onTouchEvent(event);
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (markdownView != null && markdownView.canGoBack())
            markdownView.goBack();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_view, menu);
    }

    // Register and unregister DeleteNotesReceiver
    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Override default Android "up" behavior to instead mimic the back button
                getActivity().onBackPressed();
                return true;

            // Edit button
            case R.id.action_edit:
                Bundle bundle = new Bundle();
                bundle.putString("filename", filename);

                Fragment fragment = new NoteEditFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                        .commit();

                return true;

            // Delete button
            case R.id.action_delete:
                listener.showDeleteDialog();
                return true;

            // Share menu item
            case R.id.action_share:
                // Send a share intent
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, contentsOnLoad);
                intent.setType("text/plain");

                // Verify that the intent will resolve to an activity, and send
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));

                return true;

            // Export menu item
            case R.id.action_export:
                listener.exportNote(filename);
                return true;

            // Print menu item
            case R.id.action_print:
                listener.printNote(contentsOnLoad);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onDeleteDialogPositiveClick() {
        // User touched the dialog's positive button
        deleteNote(filename);
        showToast(R.string.note_deleted);

        if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-large")) {
            Intent listNotesIntent = new Intent();
            listNotesIntent.setAction("ir.mahdiparastesh.notepad.LIST_NOTES");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(listNotesIntent);
        }

        // Add NoteListFragment or WelcomeFragment
        Fragment fragment;
        if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-normal"))
            fragment = new NoteListFragment();
        else fragment = new WelcomeFragment();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noteViewEdit, fragment, "NoteListFragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }

    // Nested class used to listen for keyboard shortcuts
    public void dispatchKeyShortcutEvent(int keyCode) {
        switch (keyCode) {

            // CTRL+E: Edit
            case KeyEvent.KEYCODE_E:
                Bundle bundle = new Bundle();
                bundle.putString("filename", filename);

                Fragment fragment = new NoteEditFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                        .commit();
                break;

            // CTRL+D: Delete
            case KeyEvent.KEYCODE_D:
                // Show delete dialog
                listener.showDeleteDialog();
                break;

            // CTRL+H: Share
            case KeyEvent.KEYCODE_H:
                // Send a share intent
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, contentsOnLoad);
                shareIntent.setType("text/plain");

                // Verify that the intent will resolve to an activity, and send
                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));

                break;
        }
    }

    private void deleteNote(String filename) {
        // Build the pathname to delete file, then perform delete operation
        File fileToDelete = new File(getActivity().getFilesDir() + File.separator + filename);
        fileToDelete.delete();
    }

    private void showToast(int message) {
        Toast toast = Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showToastLong(int message) {
        Toast toast = Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_LONG);
        toast.show();
    }

    public void onBackPressed() {
        // Add NoteListFragment or WelcomeFragment
        Fragment fragment;
        if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-normal"))
            fragment = new NoteListFragment();
        else
            fragment = new WelcomeFragment();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.noteViewEdit, fragment, "NoteListFragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }

    public interface Listener {
        void showDeleteDialog();

        String loadNote(String filename) throws IOException;

        String loadNoteTitle(String filename) throws IOException;

        void exportNote(String filename);

        void printNote(String contentToPrint);
    }

    // Receiver used to close fragment when a note is deleted
    public class DeleteNotesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] filesToDelete = intent.getStringArrayExtra("files");

            for (Object file : filesToDelete) {
                if (filename.equals(file)) {
                    // Add NoteListFragment or WelcomeFragment
                    Fragment fragment;
                    if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-normal"))
                        fragment = new NoteListFragment();
                    else
                        fragment = new WelcomeFragment();

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.noteViewEdit, fragment, "NoteListFragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                }
            }
        }
    }

    public String getFilename() {
        return getArguments().getString("filename");
    }
}
