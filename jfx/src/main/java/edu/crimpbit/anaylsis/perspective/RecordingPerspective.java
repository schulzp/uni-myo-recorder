/************************************************************************
 * Copyright (C) 2010 - 2012
 * <p>
 * [RecordingPerspective.java]
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
package edu.crimpbit.anaylsis.perspective;

import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.lifecycle.OnHide;
import org.jacpfx.api.annotations.lifecycle.OnShow;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.perspective.FXPerspective;
import org.jacpfx.rcp.util.LayoutUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import static javafx.scene.layout.Priority.ALWAYS;

/**
 * A simple perspective defining a split pane
 *
 * @author: Andy Moncsek
 * @author: Patrick Symmangk (pete.jacp@gmail.com)
 */
@Perspective(id = BasicConfig.PERSPECTIVE_RECORDING,
        name = "recordingPerspective",
        components = {
                BasicConfig.COMPONENT_LEFT,
                BasicConfig.COMPONENT_RIGHT,
                BasicConfig.STATEFUL_CALLBACK},
        resourceBundleLocation = "bundles.languageBundle")
public class RecordingPerspective implements FXPerspective {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingPerspective.class);

    @Resource
    public Context context;

    private SplitPane mainLayout;

    @Override
    public void handlePerspective(final Message<Event, Object> action,
                                  final PerspectiveLayout perspectiveLayout) {

    }


    @OnShow
    /**
     * This method will be executed when the perspective gets the focus and switches to foreground
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    public void onShow(final FXComponentLayout layout) {
        LOGGER.info("on show of RecordingPerspective");
    }

    @OnHide
    /**
     * will be executed when an active perspective looses the focus and moved to the background.
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    public void onHide(final FXComponentLayout layout) {
        LOGGER.info("on hide of RecordingPerspective");
    }

    @PostConstruct
    /**
     * @PostConstruct annotated method will be executed when component is activated.
     * @param layout
     * @param resourceBundle
     */
    public void onStartPerspective(final PerspectiveLayout perspectiveLayout, final FXComponentLayout layout,
                                   final ResourceBundle resourceBundle) {
        // define toolbars and menu entries
        JACPToolBar toolbar = layout.getRegisteredToolBar(ToolbarPosition.NORTH);
        Button pressMe = new Button(resourceBundle.getString("p2.button"));
        pressMe.setOnAction((event) -> context.send(BasicConfig.PERSPECTIVE_ANALYSIS, "show"));
        toolbar.addAllOnEnd(pressMe);
        toolbar.add(new Label(resourceBundle.getString("p1.button")));


        mainLayout = new SplitPane();
        mainLayout.setOrientation(Orientation.HORIZONTAL);
        mainLayout.setDividerPosition(0, 0.3f);
        // let them grow
        LayoutUtil.GridPaneUtil.setFullGrow(ALWAYS, mainLayout);
        // create left button menu
        GridPane leftMenu = new GridPane();
        // create main content Top
        GridPane mainContent = new GridPane();

        mainLayout.getItems().addAll(leftMenu, mainContent);
        // Register root component
        perspectiveLayout.registerRootComponent(mainLayout);
        // register left menu
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_LEFT, leftMenu);
        // register main content
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_MAIN, mainContent);
        LOGGER.info("on PostConstruct of RecordingPerspective");
    }

    /**
     * @param layout, the component layout contains references to the toolbar and the menu
     * @PreDestroy annotated method will be executed when component is deactivated.
     */
    @PreDestroy
    public void onTearDownPerspective(final FXComponentLayout layout) {
        LOGGER.info("on PreDestroy of RecordingPerspective");
    }

}
