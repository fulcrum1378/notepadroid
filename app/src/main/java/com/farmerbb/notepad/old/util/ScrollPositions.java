package com.farmerbb.notepad.old.util;

public class ScrollPositions {
    private static ScrollPositions instance = new ScrollPositions();
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
