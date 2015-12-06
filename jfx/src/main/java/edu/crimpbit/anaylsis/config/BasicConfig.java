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

import edu.crimpbit.ConnectorService;
import edu.crimpbit.RecorderService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan
public class BasicConfig implements ApplicationContextAware {

    public static final String WORKBENCH_DEFAULT = "wDefault";

    public static final String RECORDING_PERSPECTIVE = "pRecording";
    public static final String ANALYSIS_PERSPECTIVE = "pAnalysis";

    public static final String COMPONENT_LEFT = "id002";
    public static final String COMPONENT_RIGHT = "id003";

    public static final String DEVICES_VIEW = "vDevices";

    public static final String DIALOG_FRAGMENT = "fDialog";
    public static final String RECORDER_FRAGMENT = "fRecorder";

    public static final String STATELESS_CALLBACK = "id004";
    public static final String STATEFUL_CALLBACK = "id005";

    public static final String TARGET_CONTAINER_LEFT = "tLeft";
    public static final String TARGET_CONTAINER_MAIN = "tMain";

    public static final String TAB_VIEW = "vTab";

    @Bean
    public ConnectorService connectorService() {
        return new ConnectorService();
    }

    @Bean
    public RecorderService recordingService(ConnectorService connectorService) {
        return new RecorderService(connectorService);
    }

    @Bean
    public Executor executor() {
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("Background Tasks");
            return thread;
        });

        context.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                executor.shutdown();
            }
        });

        return executor;
    }

    private static ConfigurableApplicationContext context;

    public static void stop() {
        if (context != null) {
            context.close();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext) applicationContext;
        context.registerShutdownHook();
    }

}
