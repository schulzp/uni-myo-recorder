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
import edu.crimpbit.service.ConnectorService;
import edu.crimpbit.service.RecordingService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.javafx.FXMLController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Recorder UI.
 */
@FXMLController
@Scope("prototype")
public class RecorderFragment implements FXMLController.RootNodeAware<VBox>, Persistable<Recording> {

    public static final int MAX_OVERRIDES = 3;
    public static final int CHART_UPDATE_CHUNK_SIZE = 20;

    @Autowired
    private ResourceBundle bundle;

    @FXML
    private ToggleButton recordButton;

    @FXML
    private ComboBox<String> exerciseSelect;

    @FXML
    private ComboBox<String> subjectSelect;

    @FXML
    private ComboBox<Integer> durationSelect;

    @FXML
    private TilePane emgCharts;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private List<List<LineChart.Series<Number, Number>>> emgData = new ArrayList<>(8);

    private Recorder recorder;

    private int numberOfOverrides;

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

    private VBox rootNode;

    private ReadOnlyStringWrapper recordName = new ReadOnlyStringWrapper("New Recording");

    private ReadOnlyObjectWrapper<Task<Recording>> saveTask = new ReadOnlyObjectWrapper<>();

    @Override
    public void setRootNode(VBox rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public VBox getRootNode() {
        return rootNode;
    }

    @Override
    public void save() {
        applicationEventPublisher.publishEvent(new Task<Recording>() {

            @Override
            protected Recording call() throws Exception {
                Recording recording = recorder.getRecording();
                recordingService.save(recording);
                recordingService.save("recording.csv", this::updateProgress);
                return recording;
            }

        });
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return recordName;
    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
        bindRecording(recorder.getRecording());
    }

    @PostConstruct
    private void initialize() {
        createCharts();
        recordButton.textProperty().bind(
                Bindings.when(recordButton.selectedProperty())
                        .then(bundle.getString("recorder.recording"))
                        .otherwise(bundle.getString("recorder.record")));

        recordButton.selectedProperty().addListener(observable -> {
            ObservableList<Device> devices = connectorService.getDevices();
            if (recordButton.isSelected()) {
                devices.stream().filter(Device::isSelected).findFirst().ifPresent(device -> {
                    recorder.addListener(listener, MoreExecutors.directExecutor());
                    recorder.startAsync();
                });
            } else if (recorder != null && recorder.isRunning()) {
                recorder.stopAsync().awaitTerminated();
            }
        });

        subjectSelect.disableProperty().bind(recordButton.selectedProperty());
        exerciseSelect.disableProperty().bind(recordButton.selectedProperty());
    }

    private void bindRecording(Recording recording) {
        recording.setExercise(exerciseSelect.getSelectionModel().getSelectedItem());
        recording.setSubject(subjectSelect.getSelectionModel().getSelectedItem());
    }

    private void createCharts() {
        for (int emgIndex = 0; emgIndex < 8; ++emgIndex) {
            ObservableList overrideSeries = FXCollections.observableArrayList();
            for (int overrideIndex = 0; overrideIndex < MAX_OVERRIDES; ++overrideIndex) {
                XYChart.Series<Number, Number> series = new LineChart.Series<>();
                series.setName("Override " + overrideIndex);
                overrideSeries.add(series);
            }

            emgData.add(overrideSeries);

            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis(-150, 150, 50);
            yAxis.setAutoRanging(false);

            LineChart<Number, Number> emgChart = new LineChart<>(xAxis,  yAxis);
            emgChart.setAnimated(false);
            emgChart.setMaxSize(300, 150);
            emgChart.setTitle("EMG " + emgIndex);
            emgChart.setTitleSide(Side.TOP);
            emgChart.getStyleClass().add("emg-chart");
            emgChart.setData(overrideSeries);
            emgChart.applyCss();

            emgCharts.getChildren().add(emgChart);
        }
    }

    private void handleRecordingStarted() {
        Integer duration = durationSelect.getSelectionModel().getSelectedItem();
        if (duration != null && duration > 0) {
            new Timeline(new KeyFrame(Duration.seconds(duration), reached -> recorder.stopAsync())).play();
        }

        int overrideIndex = numberOfOverrides++ % MAX_OVERRIDES;

        ObservableList<Recording.EmgRecord> records = recorder.getRecords();
        records.addListener(new ListChangeListener<Recording.EmgRecord>() {

            private int emgDataRangeBegin = 0;
            private int emgDataRangeEnd = 0;

            @Override
            public void onChanged(Change<? extends Recording.EmgRecord> c) {
                while (c.next()) {
                    emgDataRangeEnd += c.getAddedSize();
                    if (emgDataRangeEnd - emgDataRangeBegin > CHART_UPDATE_CHUNK_SIZE) {
                        int begin = emgDataRangeBegin;
                        int end = emgDataRangeEnd;
                        Platform.runLater(() -> {
                            for (int emgDataOffset = begin; emgDataOffset < end; ++emgDataOffset) {
                                Recording.EmgRecord record = records.get(emgDataOffset);
                                for (int emgIndex = 0; emgIndex < 8; ++emgIndex) {
                                    XYChart.Data<Number, Number> data = new XYChart.Data<>(emgDataOffset, filter(record.getData()[emgIndex]));
                                    emgData.get(emgIndex).get(overrideIndex).getData().add(data);
                                }
                            }
                        });
                        emgDataRangeBegin = emgDataRangeEnd;
                    }
                }
            }

            private int filter(byte b) {
                return b;
            }

        });
    }

    private void handleRecordingStopped() {
        Platform.runLater(() -> {
            recordButton.setSelected(false);
        });
    }

}
