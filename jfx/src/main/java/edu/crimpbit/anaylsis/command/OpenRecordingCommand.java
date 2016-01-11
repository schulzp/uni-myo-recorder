package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Recording;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Command to open a {@link Recording}.
 */
public class OpenRecordingCommand extends OpenCommand<Recording> {

    public OpenRecordingCommand(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

}
