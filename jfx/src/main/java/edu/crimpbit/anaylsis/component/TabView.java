/************************************************************************
 * Copyright (C) 2010 - 2012
 * <p>
 * [ComponentRight.java]
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
package edu.crimpbit.anaylsis.component;

import edu.crimpbit.anaylsis.config.ApplicationConfiguration;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * A simple JacpFX UI component
 *
 * @author Andy Moncsek
 */
@DeclarativeView(id = ApplicationConfiguration.TAB_VIEW, name = "TabView",
        active = true,
        viewLocation = "/fxml/TabView.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = ApplicationConfiguration.TARGET_CONTAINER_MAIN)
public class TabView implements FXComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabView.class);

    @Resource
    private Context context;

    @FXML
    private TabPane tabPane;

    /**
     * The handle method always runs outside the main application thread. You can create new nodes,
     * execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    @Override
    public Node handle(final Message<Event, Object> message) {
        return null;
    }

    /**
     * The postHandle method runs always in the main application thread.
     */
    @Override
    public Node postHandle(final Node target, final Message<Event, Object> message) {
        return null;
    }

    /**
     * The @PostConstruct annotation labels methods executed when the component switch from inactive to active state
     * @param layout
     * @param resourceBundle
     */
    @PostConstruct
    public void onPostConstructComponent(final FXComponentLayout layout, final ResourceBundle resourceBundle) {
        LOGGER.info("run on start of ComponentRight ");
    }

    /**
     * The @PreDestroy annotations labels methods executed when the component is set to inactive
     * @param layout
     */
    @PreDestroy
    public void onPreDestroyComponent(final FXComponentLayout layout) {
        LOGGER.info("run on tear down of ComponentRight ");
    }


}

