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
import javafx.fxml.FXML;
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
 * @author Andy Moncsek
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */
@Perspective(id = BasicConfig.PERSPECTIVE_ANALYSIS,
        name = "analysisPerspective",
        components = {
                BasicConfig.COMPONENT_LEFT,
                BasicConfig.COMPONENT_RIGHT,
                BasicConfig.STATELESS_CALLBACK},
        viewLocation = "/fxml/perspectiveTwo.fxml",
        resourceBundleLocation = "bundles.languageBundle")
public class AnalysisPerspective implements FXPerspective {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisPerspective.class);
    @Resource
    public Context context;
    @FXML
    private SplitPane mainLayout;
    @FXML
    private GridPane leftMenu;
    @FXML
    private GridPane mainContent;

    @Override
    /**
     * handle messages to perspective, be aware... perspective always runs on FXApplication thread
     *
     */
    public void handlePerspective(final Message<Event, Object> message,
                                  final PerspectiveLayout perspectiveLayout) {

    }


    @OnShow
    /**
     * This method will be executed when the perspective gets the focus and switches to foreground
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    public void onShow(final FXComponentLayout layout) {
        LOGGER.info("on show of AnalysisPerspective");
    }

    @OnHide
    /**
     * will be executed when an active perspective looses the focus and moved to the background.
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    public void onHide(final FXComponentLayout layout) {
        LOGGER.info("on hide of AnalysisPerspective");
    }

    @PostConstruct
    /**
     * @PostConstruct annotated method will be executed when component is activated.
     * @param perspectiveLayout , the perspective layout let you register target layouts
     * @param layout, the component layout contains references to the toolbar and the menu
     * @param resourceBundle
     */
    public void onStartPerspective(final PerspectiveLayout perspectiveLayout, final FXComponentLayout layout,
                                   final ResourceBundle resourceBundle) {
        // define toolbars and menu entries
        JACPToolBar toolbar = layout.getRegisteredToolBar(ToolbarPosition.NORTH);


        Button pressMe = new Button(resourceBundle.getString("p1.button"));
        pressMe.setOnAction((event) -> context.send(BasicConfig.PERSPECTIVE_RECORDING, "show"));
        toolbar.addAllOnEnd(pressMe);
        toolbar.add(new Label(resourceBundle.getString("p2.button")));
        // let them grow
        LayoutUtil.GridPaneUtil.setFullGrow(ALWAYS, mainLayout);
        // register left menu
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_LEFT, leftMenu);
        // register main content
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_MAIN, mainContent);
        LOGGER.info("on PostConstruct of AnalysisPerspective");
    }

    @PreDestroy
    /**
     * @PreDestroy annotated method will be executed when component is deactivated.
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    public void onTearDownPerspective(final FXComponentLayout layout) {
        LOGGER.info("on PreDestroy of AnalysisPerspective");

    }

}
