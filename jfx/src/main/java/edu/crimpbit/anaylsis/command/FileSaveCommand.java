package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Recording;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * New {@link Recording} command.
 */
public class FileSaveCommand implements Command<Recording> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void run() {
        eventPublisher.publishEvent(this);
    }

}
