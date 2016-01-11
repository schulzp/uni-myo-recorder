/*
 * **********************************************************************
 *
 *  Copyright (C) 2010 - 2014
 *
 *  [Component.java]
 *  JACPFX Project (https://github.com/JacpFX/JacpFX/)
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS"
 *  BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 *
 *
 * *********************************************************************
 */

package edu.crimpbit.anaylsis.config;

import edu.crimpbit.anaylsis.command.*;
import edu.crimpbit.anaylsis.util.ArmStringConverter;
import edu.crimpbit.anaylsis.util.DeviceStringConverter;
import edu.crimpbit.anaylsis.view.ImuView;
import edu.crimpbit.anaylsis.view.RecordingEditor;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.service.ConnectorService;
import javafx.concurrent.Task;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.javafx.EnableFXMLControllers;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

@Configuration
@EnableFXMLControllers
@ComponentScan(basePackages = { "edu.crimpbit.anaylsis.view" })
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
    public CommandService commandService(BeanFactory beanFactory) {
        return new CommandService(beanFactory);
    }

    @Bean
    public OpenControllerCommand imuViewOpenCommand(CommandService commandService, ApplicationEventPublisher applicationEventPublisher) {
        OpenControllerCommand openControllerCommand = new OpenControllerCommand(applicationEventPublisher, ImuView.class, "view.show.imu.command");
        commandService.registerCommand(openControllerCommand);
        return openControllerCommand;
    }

    @Bean
    public OpenControllerCommand fileNewCommand(CommandService commandService, ApplicationEventPublisher applicationEventPublisher) {
        OpenControllerCommand openControllerCommand = new OpenControllerCommand(applicationEventPublisher, RecordingEditor.class, "file.new.command");
        commandService.registerCommand(openControllerCommand);
        return openControllerCommand;
    }

    @Bean
    @Scope("prototype")
    public OpenRecordingCommand openRecordingCommand(ApplicationEventPublisher applicationEventPublisher) {
        return new OpenRecordingCommand(applicationEventPublisher);
    }

    @Bean
    public FileSaveCommand fileSaveCommand(CommandService commandService) {
        FileSaveCommand command = new FileSaveCommand();
        commandService.registerCommand(command);
        return command;
    }

    @Bean
    public DeviceStringConverter deviceStringConverter(ConnectorService connectorService, ArmStringConverter armStringConverter) {
        return new DeviceStringConverter(connectorService, armStringConverter);
    }

    @Bean
    public ArmStringConverter armStringConverter(ResourceBundle resourceBundle) {
        return new ArmStringConverter(resourceBundle);
    }

    @Bean
    public ControlFactory controlFactory() {
        return new ControlFactory();
    }

    private static ConfigurableApplicationContext context;

}
