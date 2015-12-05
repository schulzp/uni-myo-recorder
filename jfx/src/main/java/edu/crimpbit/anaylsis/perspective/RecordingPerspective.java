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

import edu.crimpbit.anaylsis.component.ComponentRight;
import edu.crimpbit.anaylsis.component.DevicesView;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.lifecycle.OnHide;
import org.jacpfx.api.annotations.lifecycle.OnShow;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.component.ComponentHandle;
import org.jacpfx.api.component.SubComponent;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.component.EmbeddedFXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.perspective.FXPerspective;
import org.jacpfx.rcp.registry.ComponentRegistry;
import org.jacpfx.rcp.util.LayoutUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import static javafx.scene.layout.Priority.ALWAYS;

@Perspective(id = BasicConfig.RECORDING_PERSPECTIVE,
        name = "RecordingPerspective",
        components = {
                BasicConfig.DEVICES_VIEW,
                BasicConfig.CONNECTOR_CALLBACK},
        resourceBundleLocation = "bundles.languageBundle")
public class RecordingPerspective implements FXPerspective {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingPerspective.class);

    @Resource
    public Context context;

    private SplitPane mainLayout;
    private TabPane mainTarget;

    @Override
    public void handlePerspective(final Message<Event, Object> action, final PerspectiveLayout perspectiveLayout) {
        LOGGER.info("handle perspective {}", action);

        if (action.getMessageBody().equals("new")) {
            Tab tab = new Tab();
            tab.setText("New Tab");
            SubComponent<EventHandler<Event>, Event, Object> subComponent = ComponentRegistry.findComponentByClass(ComponentRight.class);
            SubComponent<EventHandler<Event>, Event, Object> subComponent2 = ComponentRegistry.findComponentByClass(ComponentRight.class);

            if (subComponent instanceof EmbeddedFXComponent) {
                Text text = new Text();
                text.setText("text");
                tab.setContent(text);
            }
            mainTarget.getTabs().add(tab);
        }
    }

    /**
     * This method will be executed when the perspective gets the focus and switches to foreground
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    @OnShow
    public void onShow(final FXComponentLayout layout) {
        LOGGER.info("on show of RecordingPerspective");
    }

    /**
     * will be executed when an active perspective looses the focus and moved to the background.
     * @param layout, the component layout contains references to the toolbar and the menu
     */
    @OnHide
    public void onHide(final FXComponentLayout layout) {
        LOGGER.info("on hide of RecordingPerspective");
    }

    /**
     * @PostConstruct annotated method will be executed when component is activated.
     * @param layout
     * @param resourceBundle
     */
    @PostConstruct
    public void onStartPerspective(final PerspectiveLayout perspectiveLayout, final FXComponentLayout layout,
                                   final ResourceBundle resourceBundle) {
        // define toolbars and menu entries
        JACPToolBar toolbar = layout.getRegisteredToolBar(ToolbarPosition.NORTH);
        Button pressMe = new Button(resourceBundle.getString("perspective.analysis"));
        pressMe.setOnAction((event) -> context.send(BasicConfig.ANALYSIS_PERSPECTIVE, "show"));
        toolbar.addAllOnEnd(pressMe);
        toolbar.add(new Label(resourceBundle.getString("perspective.recording")));

        mainLayout = new SplitPane();
        mainLayout.setOrientation(Orientation.HORIZONTAL);
        mainLayout.setDividerPosition(0, 0.3f);

        LayoutUtil.GridPaneUtil.setFullGrow(ALWAYS, mainLayout);

        Node leftTarget = new VBox();
        mainTarget = new TabPane();

        mainLayout.getItems().addAll(leftTarget, mainTarget);
        // Register root component
        perspectiveLayout.registerRootComponent(mainLayout);
        // register left target
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_LEFT, leftTarget);
        // register main target
        perspectiveLayout.registerTargetLayoutComponent(BasicConfig.TARGET_CONTAINER_MAIN, mainTarget);
    }

}
