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

import edu.crimpbit.anaylsis.config.ApplicationConfiguration;
import edu.crimpbit.anaylsis.fragment.RecorderFragment;
import edu.crimpbit.service.RecordingService;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.lifecycle.OnHide;
import org.jacpfx.api.annotations.lifecycle.OnShow;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.components.managedFragment.ManagedFragmentHandler;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.perspective.FXPerspective;
import org.jacpfx.rcp.util.LayoutUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ResourceBundle;
import java.util.concurrent.Executor;

import static javafx.scene.layout.Priority.ALWAYS;

@Perspective(id = ApplicationConfiguration.RECORDING_PERSPECTIVE,
        name = "RecordingPerspective",
        components = { ApplicationConfiguration.DEVICES_VIEW },
        resourceBundleLocation = "bundles.languageBundle")
public class RecordingPerspective implements FXPerspective {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingPerspective.class);

    @Autowired
    private Executor executor;

    @Autowired
    private RecordingService recordingService;

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

            ManagedFragmentHandler<RecorderFragment> recorderFragmentHandler = context.getManagedFragmentHandler(RecorderFragment.class);
            tab.setContent(recorderFragmentHandler.getFragmentNode());

            mainTarget.getTabs().add(tab);
        } else if (action.getMessageBody().equals("save")) {
            executor.execute(new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    recordingService.save("emg-recording", (of, total) -> {});
                    return null;
                }

            });
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
        pressMe.setOnAction((event) -> context.send(ApplicationConfiguration.ANALYSIS_PERSPECTIVE, "show"));
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
        perspectiveLayout.registerTargetLayoutComponent(ApplicationConfiguration.TARGET_CONTAINER_LEFT, leftTarget);
        // register main target
        perspectiveLayout.registerTargetLayoutComponent(ApplicationConfiguration.TARGET_CONTAINER_MAIN, mainTarget);
    }

}
