package edu.crimpbit.anaylsis.command;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Simple command to open a controller.
 */
public class OpenControllerCommand<C> extends OpenCommand<Class<C>> {

    private final String id;

    public OpenControllerCommand(ApplicationEventPublisher applicationEventPublisher, Class<C> type, String id) {
        super(applicationEventPublisher);
        setElement(type);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

}
