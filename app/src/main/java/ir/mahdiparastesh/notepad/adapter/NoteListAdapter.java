package ir.mahdiparastesh.notepad.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.mahdiparastesh.notepad.R;
import ir.mahdiparastesh.notepad.utils.NoteListItem;
import ir.mahdiparastesh.notepad.utils.ThemeManager;

public class NoteListAdapter extends ArrayAdapter<NoteListItem> {
    public NoteListAdapter(Context context, ArrayList<NoteListItem> notes) {
        super(context, R.layout.row_layout, notes);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.row_layout, parent, false);

        TextView noteTitle = convertView.findViewById(R.id.noteTitle);
        noteTitle.setText(getItem(i).getNote());

        SharedPreferences pref = getContext().getSharedPreferences(
                getContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
        ThemeManager.setTextColor(getContext(), noteTitle);
        ThemeManager.setFont(pref, noteTitle);
        ThemeManager.setFontSize(pref, noteTitle);

        return convertView;
    }
}
