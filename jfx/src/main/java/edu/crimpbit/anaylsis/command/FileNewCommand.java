package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Device;
import edu.crimpbit.Recording;
import edu.crimpbit.service.ConnectorService;
import edu.crimpbit.service.RecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Global new command.
 */
public class FileNewCommand implements Command<Recording> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ConnectorService connectorService;

    @Override
    public void run() {
        connectorService.getDevices().stream().filter(Device::isSelected).findFirst().ifPresent(device -> {
            OpenCommand<Object> openCommand = new OpenCommand<>(eventPublisher);
            openCommand.setElement(recordingService.createRecorder(device));
            eventPublisher.publishEvent(openCommand);
        });
    }

}
