package ir.mahdiparastesh.notepad.utils;

public class ScrollPositions {
    private static final ScrollPositions instance = new ScrollPositions();
    private int position = 0;

    private ScrollPositions() {
    }

    public static ScrollPositions getInstance() {
        return instance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
