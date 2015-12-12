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

import edu.crimpbit.config.CoreConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.javafx.EnableFXMLControllers;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableFXMLControllers
@ComponentScan(basePackages = { "edu.crimpbit.anaylsis.view" })
@Import({ CoreConfiguration.class })
public class AnalysisApplicationConfiguration {

    @Bean(destroyMethod = "shutdown")
    public Executor executor() {
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("Background Tasks");
            return thread;
        });

        return executor;
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

    private static ConfigurableApplicationContext context;

}
