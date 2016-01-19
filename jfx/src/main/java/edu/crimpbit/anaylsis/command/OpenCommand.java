package edu.crimpbit.anaylsis.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

/**
 * Prototype open command.
 * @param <T> type of the content to be opened
 */
public class OpenCommand<T> implements Command {

    private final ApplicationEventPublisher applicationEventPublisher;

    private Optional<T> content = Optional.empty();

    @Autowired
    public OpenCommand(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setContent(T content) {
        this.content = Optional.ofNullable(content);
    }

    @Override
    public void run() {
        applicationEventPublisher.publishEvent(this);
    }

    public Optional<T> getContent() {
        return content;
    }

}
