package com.farmerbb.notepad.old.util;

import java.text.Collator;
import java.util.Comparator;

public class NoteListItem {
    public static Comparator<NoteListItem> NoteComparatorTitle =
            (arg1, arg2) -> Collator.getInstance().compare(arg1.getNote(), arg2.getNote());
    private final String note;

    public NoteListItem(String note, String date) {
        this.note = note;
        this.date = date;
    }
    private final String date;

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
