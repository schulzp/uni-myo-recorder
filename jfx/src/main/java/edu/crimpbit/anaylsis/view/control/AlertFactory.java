package edu.crimpbit.anaylsis.view.control;

import edu.crimpbit.anaylsis.scene.SceneHolder;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * {@link Alert} factory.
 */
@Component
public class AlertFactory {

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Autowired
    private SceneHolder sceneHolder;

    public Alert confirm(String message, String...args) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, messageSourceAccessor.getMessage(message, args));
        sceneHolder.get().ifPresent(scene -> alert.initOwner(scene.getWindow()));
        alert.initModality(Modality.WINDOW_MODAL);
        return alert;
    }

}
