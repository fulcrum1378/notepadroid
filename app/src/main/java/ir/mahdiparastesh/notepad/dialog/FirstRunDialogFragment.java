package ir.mahdiparastesh.notepad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ir.mahdiparastesh.notepad.R;

public class FirstRunDialogFragment extends DialogFragment {
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
        builder.setMessage(R.string.first_run)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.action_close, (dialog, id) -> listener.onFirstRunDialogPositiveClick());
        setCancelable(false);
        return builder.create();
    }

    public interface Listener {
        void onFirstRunDialogPositiveClick();
    }
}
