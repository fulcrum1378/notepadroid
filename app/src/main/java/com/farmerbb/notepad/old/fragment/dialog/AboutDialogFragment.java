package com.farmerbb.notepad.old.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.farmerbb.notepad.BuildConfig;
import com.farmerbb.notepad.R;

import java.util.Calendar;
import java.util.TimeZone;

public class AboutDialogFragment extends DialogFragment {

    TextView textView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.fragment_dialogs, null);

        builder.setView(view)
        .setTitle(R.string.dialog_about_title)
        .setPositiveButton(R.string.action_close, null);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Denver"));
        calendar.setTimeInMillis(BuildConfig.TIMESTAMP);

        int year = calendar.get(Calendar.YEAR);

        textView = view.findViewById(R.id.dialogMessage);
        textView.setText(getString(R.string.dialog_about_message, year));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
