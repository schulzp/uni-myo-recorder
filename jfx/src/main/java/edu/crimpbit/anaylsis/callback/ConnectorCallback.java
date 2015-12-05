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

import edu.crimpbit.ConnectorService;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Component(id = BasicConfig.CONNECTOR_CALLBACK,
        name = "connectorCallback",
        active = true)
public class ConnectorCallback implements CallbackComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorCallback.class);

    public static final String COMMAND_REFRESH = "connector.refresh";
    public static final String COMMAND_REFRESH_DONE = COMMAND_REFRESH + ".done";

    @Autowired
    private ConnectorService connectorService;

    @Resource
    private Context context;

    @Override
    public Object handle(final Message<Event, Object> message) {
        if (message.messageBodyEquals(COMMAND_REFRESH)) {
            connectorService.refresh();
            return COMMAND_REFRESH + ".done";
        }
        return null;
    }

}
