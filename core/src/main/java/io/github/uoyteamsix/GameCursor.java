package io.github.uoyteamsix;

public enum GameCursor {
    POINTER("cursors/pointer.png"),
    HAND_OPEN("cursors/hand_open.png"),
    ZOOM_IN("cursors/zoom_in.png"),
    ZOOM_OUT("cursors/zoom_out.png");

    private final String path;

    GameCursor(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
