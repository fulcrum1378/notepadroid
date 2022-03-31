package com.farmerbb.notepad.old.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.farmerbb.notepad.R;
import com.farmerbb.notepad.old.managers.ThemeManager;
import com.farmerbb.notepad.old.util.NoteListItem;

import java.util.ArrayList;

public class NoteListAdapter extends ArrayAdapter<NoteListItem> {
    public NoteListAdapter(Context context, ArrayList<NoteListItem> notes) {
        super(context, R.layout.row_layout, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NoteListItem item = getItem(position);
        String note = item.getNote();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);

        // Lookup view for data population
        TextView noteTitle = convertView.findViewById(R.id.noteTitle);

        // Populate the data into the template view using the data object
        noteTitle.setText(note);

        // Apply theme
        SharedPreferences pref = getContext().getSharedPreferences(
                getContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String theme = pref.getString("theme", "light-sans");

        ThemeManager.setTextColor(getContext(), theme, noteTitle);
        ThemeManager.setFont(pref, noteTitle);
        ThemeManager.setFontSize(pref, noteTitle);

        // Return the completed view to render on screen
        return convertView;
    }
}
