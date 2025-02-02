package ir.mahdiparastesh.notepad.fragment;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ir.mahdiparastesh.notepad.R;
import ir.mahdiparastesh.notepad.activity.SettingsActivity;
import ir.mahdiparastesh.notepad.dialog.AboutDialogFragment;

public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_alt, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set values
        setRetainInstance(true);
        setHasOptionsMenu(true);

        // Animate elevation change
        if (getActivity().findViewById(R.id.layoutMain).getTag().equals("main-layout-large")) {
            LinearLayout noteViewEdit = getActivity().findViewById(R.id.noteViewEdit);
            LinearLayout noteList = getActivity().findViewById(R.id.noteList);

            noteViewEdit.animate().z(0f);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                noteList.animate().z(getResources().getDimensionPixelSize(R.dimen.note_list_elevation_land));
            else noteList.animate().z(getResources().getDimensionPixelSize(R.dimen.note_list_elevation));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Read preferences
        SharedPreferences prefMain = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Before we do anything else, check for a saved draft; if one exists, load it
        if (prefMain.getLong("draft-name", 0) != 0) {
            Bundle bundle = new Bundle();
            bundle.putString("filename", "draft");

            Fragment fragment = new NoteEditFragment();
            fragment.setArguments(bundle);

            // Add NoteEditFragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            // Change window title
            String title = getResources().getString(R.string.app_name);

            getActivity().setTitle(title);

            Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(getActivity(),
                    R.drawable.ic_recents_logo)).getBitmap();

            ActivityManager.TaskDescription taskDescription =
                    new ActivityManager.TaskDescription(title, bitmap,
                            ContextCompat.getColor(getActivity(), R.color.primary));
            getActivity().setTaskDescription(taskDescription);

            // Don't show the Up button in the action bar, and disable the button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

            // Floating action button
            FloatingActionButton floatingActionButton = getActivity().findViewById(
                    R.id.button_floating_action_welcome);
            floatingActionButton.setImageResource(R.drawable.ic_action_new);
            floatingActionButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("filename", "new");

                Fragment fragment = new NoteEditFragment();
                fragment.setArguments(bundle);

                // Add NoteEditFragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            });

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.action_import:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"text/plain", "text/html", "text/x-markdown"});
                intent.setType("*/*");

                try {
                    getActivity().startActivityForResult(intent, 42);
                } catch (ActivityNotFoundException e) {
                    showToast(R.string.error_importing_notes);
                }
                return true;
            case R.id.action_about:
                DialogFragment aboutFragment = new AboutDialogFragment();
                aboutFragment.show(getActivity().getSupportFragmentManager(), "about");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dispatchKeyShortcutEvent(int keyCode) {
        switch (keyCode) {
            // CTRL+N: New Note
            case KeyEvent.KEYCODE_N:
                Bundle bundle = new Bundle();
                bundle.putString("filename", "new");

                Fragment fragment = new NoteEditFragment();
                fragment.setArguments(bundle);

                // Add NoteEditFragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, "NoteEditFragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
        }
    }

    public void onBackPressed() {
        getActivity().finish();
    }

    public void showFab() {
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.button_floating_action_welcome);
        floatingActionButton.show();
    }

    public void hideFab() {
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.button_floating_action_welcome);
        floatingActionButton.hide();
    }

    private void showToast(int message) {
        Toast toast = Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_SHORT);
        toast.show();
    }
}
