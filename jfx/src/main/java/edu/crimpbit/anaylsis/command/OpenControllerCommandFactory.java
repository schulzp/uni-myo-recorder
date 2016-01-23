package edu.crimpbit.anaylsis.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.javafx.FXMLControllerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class OpenControllerCommandFactory {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final FXMLControllerFactory controllerFactory;

    private final CommandService commandService;

    private final Map<Class<?>, Class<?>> contentControllerMap = new HashMap<>();

    public OpenControllerCommandFactory(ApplicationEventPublisher applicationEventPublisher, FXMLControllerFactory controllerFactory, CommandService commandService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.controllerFactory = controllerFactory;
        this.commandService = commandService;
    }

    public void map(Class<?> contentClass, Class<?> controllerClass) {
        contentControllerMap.put(contentClass, controllerClass);
    }

    public <C> OpenControllerCommand<?, C> create(Class<C> type, String id) {
        OpenControllerCommand command = new OpenControllerCommand(applicationEventPublisher, controllerFactory, type, id);
        commandService.registerCommand(command);
        return command;
    }

    public <C, T> OpenControllerCommand<T, C> create(Class<C> type, T content) {
        return new OpenControllerCommand(applicationEventPublisher, controllerFactory, type, content);
    }

    public <T> OpenControllerCommand<T, ?> create(T content) {
        Class<?> controllerType = contentControllerMap.get(content.getClass());
        if (controllerType == null) {
            throw new NoSuchElementException("No controller for " + content.getClass());
        }
        return create(controllerType, content);
    }

}
