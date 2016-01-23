package edu.crimpbit.anaylsis.config;

import edu.crimpbit.Recording;
import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.command.*;
import edu.crimpbit.anaylsis.selection.SelectionService;
import edu.crimpbit.anaylsis.view.ImuView;
import edu.crimpbit.anaylsis.view.RecordingEditor;
import edu.crimpbit.anaylsis.view.SubjectEditor;
import edu.crimpbit.anaylsis.view.control.AlertFactory;
import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.repository.RepositoryProvider;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.javafx.EnableFXMLControllers;
import org.springframework.javafx.FXMLControllerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

@Configuration
@EnableFXMLControllers
@ComponentScan(basePackages = { "edu.crimpbit.anaylsis" })
@Import({ CoreConfiguration.class })
public class AnalysisApplicationConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("Background Task");
            return thread;
        });

        return executor;
    }

    @Bean
    public <T> Function<Task<T>, Future<T>> taskConsumer(ExecutorService executorService) {
        return new Function<Task<T>, Future<T>>() {

            @Override
            @EventListener(condition = "!#task.done && !#task.running")
            public Future<T> apply(Task<T> task) {
                executorService.submit(task);
                return null;
            }

        };
    }

    @Bean(name = "i18n")
    public ReloadableResourceBundleMessageSource messageSource(ApplicationContext parentMessageSource) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setParentMessageSource(parentMessageSource);
        messageSource.setBasename("classpath:bundles/i18n");
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(@Qualifier("i18n") MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource);
    }

    @Bean
    public OpenControllerCommand imuViewOpenCommand(OpenControllerCommandFactory factory) {
        return factory.create(ImuView.class, "view.show.imu.command");
    }

    @Bean
    public OpenControllerCommand fileNewCommand(OpenControllerCommandFactory factory) {
        return factory.create(RecordingEditor.class, "file.new.command");
    }

    @Bean
    public OpenControllerCommand fileNewSubjectCommand(OpenControllerCommandFactory factory) {
        return factory.create(SubjectEditor.class, "file.new.subject.command");
    }

    @Bean
    public StartRecordingWizardCommand startRecordingWizardCommand(CommandService commandService) {
        StartRecordingWizardCommand command = new StartRecordingWizardCommand();
        commandService.registerCommand(command);
        return command;
    }

    @Bean
    public OpenControllerCommandFactory openControllerCommandFactory(ApplicationEventPublisher applicationEventPublisher, FXMLControllerFactory controllerFactory, CommandService commandService) {
        OpenControllerCommandFactory factory = new OpenControllerCommandFactory(applicationEventPublisher, controllerFactory, commandService);
        factory.map(Recording.class, RecordingEditor.class);
        factory.map(Subject.class, SubjectEditor.class);
        return factory;
    }

    @Bean
    public FileSaveCommand fileSaveCommand(CommandService commandService) {
        FileSaveCommand command = new FileSaveCommand();
        commandService.registerCommand(command);
        return command;
    }

    @Bean
    public FileDeleteCommand fileDeleteCommand(CommandService commandService, SelectionService selectionService, RepositoryProvider repositoryProvider, AlertFactory alertFactory) {
        FileDeleteCommand fileDeleteCommand = new FileDeleteCommand(selectionService, repositoryProvider, alertFactory);
        commandService.registerCommand(fileDeleteCommand);
        return fileDeleteCommand;
    }

}
