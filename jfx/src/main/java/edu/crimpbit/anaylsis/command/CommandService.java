package edu.crimpbit.anaylsis.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Command execution service.
 */
public class CommandService {

    private Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getId(), command);
    }

    public void execute(String commandId) {
        Command command = commands.get(commandId);
        if (command != null) {
            command.run();
        }
    }

}
