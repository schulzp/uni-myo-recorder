package edu.crimpbit.anaylsis.util;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * {@link MouseEvent} utility functions.
 */
public class MouseEventUtils {

    private static final int DOUBLE_CLICK_COUNT = 2;

    public static boolean isDoubleClick(MouseEvent event) {
        return event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == DOUBLE_CLICK_COUNT;
    }

}
