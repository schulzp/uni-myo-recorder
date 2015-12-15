package edu.crimpbit.anaylsis.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Global save command.
 */
public class FileSaveCommand implements Command {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void run() {
        eventPublisher.publishEvent(this);
    }

}
