package ir.mahdiparastesh.notepad.utils;

import java.text.Collator;
import java.util.Comparator;

public class NoteListItem {
    public static Comparator<NoteListItem> NoteComparatorTitle =
            (arg1, arg2) -> Collator.getInstance().compare(arg1.getNote(), arg2.getNote());
    private final String note;
    private final String date;

    public NoteListItem(String note, String date) {
        this.note = note;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
