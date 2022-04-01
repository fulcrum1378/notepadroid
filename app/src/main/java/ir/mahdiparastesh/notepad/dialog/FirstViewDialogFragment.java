package ir.mahdiparastesh.notepad.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ir.mahdiparastesh.notepad.R;

public class FirstViewDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.Theme_Notepad_Dialog));
        builder.setMessage(R.string.first_view)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.action_close, null);
        return builder.create();
    }
}
