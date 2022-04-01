package ir.mahdiparastesh.notepad.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ir.mahdiparastesh.notepad.R;

public class BackButtonDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    Listener listener;

    // Override the Fragment.onAttach() method to instantiate the Listener
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity + " must implement Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_save_changes)
                .setTitle(R.string.dialog_save_button_title)
                .setPositiveButton(R.string.action_save, (dialog, id) ->
                        listener.onBackDialogPositiveClick(getArguments().getString("filename")))
                .setNegativeButton(R.string.action_discard, (dialog, id) ->
                        listener.onBackDialogNegativeClick(getArguments().getString("filename")));

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this fragment must
     * implement this interface in order to receive event call backs. */
    public interface Listener {
        void onBackDialogPositiveClick(String filename);

        void onBackDialogNegativeClick(String filename);
    }
}
