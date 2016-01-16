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

package edu.crimpbit.anaylsis.view;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import edu.crimpbit.Device;
import edu.crimpbit.Recorder;
import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.service.RecordingService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Recorder UI.
 */
@Controller
@Scope("prototype")
public class RecorderFragment {

    private final ReadOnlyObjectWrapper<Recording> recording = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyBooleanWrapper currentlyRecording = new ReadOnlyBooleanWrapper(false);

    private final Timer timer = new Timer(true);

    private final Service.Listener listener = new Service.Listener() {

        @Override
        public void running() {
            handleRecordingStarted();
        }

        @Override
        public void terminated(Service.State from) {
            handleRecordingStopped();
        }

    };

    @FXML
    private ToggleButton recordButton;

    @FXML
    private ComboBox<Integer> durationSelect;

    @FXML
    private ComboBox<Device> deviceSelect;

    @Autowired
    private ControlFactory controlFactory;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ResourceBundle bundle;

    private Recorder recorder;

    public ReadOnlyObjectProperty<Recording> recordingProperty() {
        return recording;
    }

    public ReadOnlyBooleanWrapper currentlyRecordingProperty() {
        return currentlyRecording;
    }

    @FXML
    private void initialize() {
        controlFactory.initializeDeviceComboBox(deviceSelect);

        recordButton.disableProperty().bind(
                Bindings.isNull(deviceSelect.getSelectionModel().selectedItemProperty()));

        recordButton.textProperty().bind(
                Bindings.when(recordButton.selectedProperty())
                        .then(bundle.getString("recorder.recording"))
                        .otherwise(bundle.getString("recorder.record")));

        deviceSelect.disableProperty().bind(recordButton.selectedProperty());
        durationSelect.disableProperty().bind(recordButton.selectedProperty());

        recordButton.selectedProperty().addListener(observable -> {
            if (recordButton.isSelected()) {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Device device = deviceSelect.getSelectionModel().getSelectedItem();
                        recorder = recordingService.createRecorder(device);
                        recording.set(recorder.getRecording());
                        recorder.addListener(listener, MoreExecutors.directExecutor());
                        recorder.startAsync();
                    }

                }, TimeUnit.SECONDS.toMillis(3));
            } else if (recorder != null && recorder.isRunning()) {
                recorder.stopAsync().awaitTerminated();
            }
        });
    }

    private void handleRecordingStarted() {
        Integer duration = durationSelect.getSelectionModel().getSelectedItem();
        if (duration != null && duration > 0) {
            new Timeline(new KeyFrame(Duration.seconds(duration), reached -> recorder.stopAsync())).play();
        }
        currentlyRecording.set(true);
    }

    private void handleRecordingStopped() {
        Platform.runLater(() -> {
            recordButton.setSelected(false);
        });
        currentlyRecording.set(false);
    }

}
