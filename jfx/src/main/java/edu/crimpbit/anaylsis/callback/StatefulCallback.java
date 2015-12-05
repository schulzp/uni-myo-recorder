/************************************************************************
 * Copyright (C) 2010 - 2012
 * <p>
 * [StatefulCallback.java]
 * AHCP Project (http://jacp.googlecode.com)
 * All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ************************************************************************/
package edu.crimpbit.anaylsis.callback;

import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * a stateful JacpFX component
 *
 * @author Andy Moncsek
 */
@Component(id = BasicConfig.STATEFUL_CALLBACK,
        name = "statefulCallback",
        active = true,
        localeID = "en_US",
        resourceBundleLocation = "bundles.languageBundle")
public class StatefulCallback implements CallbackComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatefulCallback.class);

    @Resource
    private Context context;

    @Override
    public Object handle(final Message<Event, Object> message) {
        if (!message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            context.setReturnTarget(BasicConfig.RECORDING_PERSPECTIVE.concat(".").concat(BasicConfig.COMPONENT_RIGHT));
            return "Hello: " + message.getTypedMessageBody(String.class) + " from StatefulCallback";
        }
        return null;
    }

    @PreDestroy
    /**
     * The @PreDestroy annotations labels methods executed when the component is set to inactive
     */
    public void onPreDestroyComponent() {
        LOGGER.info("run on tear down of StatefulCallback ");

    }

    @PostConstruct
    /**
     * The @PostConstruct annotation labels methods executed when the component switch from inactive to active state
     * @param resourceBundle
     */
    public void onPostConstructComponent(final ResourceBundle resourceBundle) {
        LOGGER.info("run on start of StatefulCallback ");

    }

}
