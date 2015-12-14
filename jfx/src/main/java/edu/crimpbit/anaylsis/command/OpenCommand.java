package edu.crimpbit.anaylsis.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;

/**
 * Prototype open command.
 * @param <T> type of the element to be opened
 */
@Scope("prototype")
public class OpenCommand<T> implements Command<T> {

    private final ApplicationEventPublisher applicationEventPublisher;

    private T element;

    @Autowired
    public OpenCommand(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setElement(T element) {
        this.element = element;
    }

    @Override
    public void run() {
        applicationEventPublisher.publishEvent(this);
    }

    public T getElement() {
        return element;
    }
}
