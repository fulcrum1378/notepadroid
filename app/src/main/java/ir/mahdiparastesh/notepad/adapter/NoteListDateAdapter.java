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

public class NoteListDateAdapter extends ArrayAdapter<NoteListItem> {
    public NoteListDateAdapter(Context context, ArrayList<NoteListItem> notes) {
        super(context, R.layout.row_layout_date, notes);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        NoteListItem item = getItem(i);
        String note = item.getNote();
        String date = item.getDate();

        if (convertView == null) convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.row_layout_date, parent, false);

        TextView noteTitle = convertView.findViewById(R.id.noteTitle);
        TextView noteDate = convertView.findViewById(R.id.noteDate);
        noteTitle.setText(note);
        noteDate.setText(date);

        SharedPreferences pref = getContext().getSharedPreferences(
                getContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
        ThemeManager.setTextColor(getContext(), noteTitle);
        ThemeManager.setTextColorDate(getContext(), noteDate);
        ThemeManager.setFont(pref, noteTitle);
        ThemeManager.setFont(pref, noteDate);
        ThemeManager.setFontSize(pref, noteTitle);
        ThemeManager.setFontSizeDate(pref, noteDate);

        return convertView;
    }
}
