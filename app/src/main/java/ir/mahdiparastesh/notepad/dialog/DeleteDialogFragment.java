package ir.mahdiparastesh.notepad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ir.mahdiparastesh.notepad.R;

public class DeleteDialogFragment extends DialogFragment {
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
        builder.setMessage(R.string.dialog_are_you_sure)
                .setTitle(getArguments().getInt("dialog_title"))
                .setPositiveButton(R.string.action_delete, (dialog, id) -> listener.onDeleteDialogPositiveClick())
                .setNegativeButton(R.string.action_cancel, null);
        return builder.create();
    }

    public interface Listener {
        void onDeleteDialogPositiveClick();
    }
}
