package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Recording;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Global new command.
 */
public class FileNewCommand implements Command {

    @Autowired
    private CommandService commandService;

    @Override
    public void run() {
        OpenCommand openCommand = commandService.getCommand(OpenCommand.class);
        openCommand.setElement(Recording.class);
        commandService.execute(openCommand);
    }

}
