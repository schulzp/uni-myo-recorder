package edu.crimpbit.anaylsis.command;

import edu.crimpbit.anaylsis.view.Named;
import edu.crimpbit.anaylsis.view.Persistable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.javafx.FXMLControllerFactory;

import java.util.Optional;

/**
 * Simple command to open a controller.
 */
public class OpenControllerCommand<T, C> extends OpenCommand<T> {

    private final FXMLControllerFactory<C> controllerFactory;
    private final Class<C> type;
    private final String id;

    public OpenControllerCommand(ApplicationEventPublisher applicationEventPublisher, FXMLControllerFactory<C> controllerFactory, Class<C> type, T content) {
        this(applicationEventPublisher, controllerFactory, type, "open." + content.getClass().getSimpleName() + "." + content.hashCode());
        setContent(content);
    }

    public OpenControllerCommand(ApplicationEventPublisher applicationEventPublisher, FXMLControllerFactory<C> controllerFactory, Class<C> type, String id) {
        super(applicationEventPublisher);
        this.controllerFactory = controllerFactory;
        this.type = type;
        this.id = id;
    }

    public C createController() {
        C controller = controllerFactory.call(type);
        if (controller instanceof Named) {
            getContent().ifPresent(((Persistable) controller)::setPersistable);
        }
        return controller;
    }

    @Override
    public Optional<T> getContent() {
        return super.getContent();
    }

    @Override
    public String getId() {
        return id;
    }

}
