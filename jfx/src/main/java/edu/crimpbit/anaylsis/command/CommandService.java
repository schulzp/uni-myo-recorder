package edu.crimpbit.anaylsis.command;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Command execution service.
 */
@Service
public class CommandService {

    private final Map<String, Command> commands = new HashMap<>();

    private final BeanFactory beanFactory;

    @Autowired
    public CommandService(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public <T> T getCommand(Class<T> type) {
        return beanFactory.getBean(type);
    }

    public void registerCommand(Command command) {
        commands.putIfAbsent(command.getId(), command);
    }

    public void execute(String commandId) {
        execute(commands.get(commandId));
    }

    public void execute(Command command) {
        if (command != null) {
            command.run();
        }
    }

}
