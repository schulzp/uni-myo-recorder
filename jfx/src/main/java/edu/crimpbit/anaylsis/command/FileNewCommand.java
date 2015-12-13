package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.event.OpenInNewTab;
import edu.crimpbit.anaylsis.view.RecorderFragment;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.javafx.FXMLControllerFactory;

/**
 * New {@link Recording} command.
 */
public class FileNewCommand implements Command<Recording> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FXMLControllerFactory controllerFactory;

    @Override
    public void run() {
        RecorderFragment recorderFragment = (RecorderFragment) controllerFactory.call(RecorderFragment.class);
        OpenInNewTab event = new OpenInNewTab(this, recorderFragment.getRootNode(), new SimpleStringProperty("New Recording"));
        eventPublisher.publishEvent(event);
    }

}
