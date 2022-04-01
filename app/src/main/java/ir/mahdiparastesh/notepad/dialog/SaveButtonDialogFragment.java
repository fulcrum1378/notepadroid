package ir.mahdiparastesh.notepad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ir.mahdiparastesh.notepad.R;

public class SaveButtonDialogFragment extends DialogFragment {
    Listener listener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement Listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Theme_Notepad_Dialog));
        builder.setMessage(R.string.dialog_save_changes)
                .setTitle(R.string.dialog_save_button_title)
                .setPositiveButton(R.string.action_save, (dialog, id) -> listener.onSaveDialogPositiveClick())
                .setNegativeButton(R.string.action_discard, (dialog, id) -> listener.onSaveDialogNegativeClick());
        return builder.create();
    }

    public interface Listener {
        void onSaveDialogPositiveClick();

        void onSaveDialogNegativeClick();
    }
}
