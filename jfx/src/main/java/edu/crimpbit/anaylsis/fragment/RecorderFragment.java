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

package edu.crimpbit.anaylsis.fragment;

import edu.crimpbit.ConnectorService;
import edu.crimpbit.Device;
import edu.crimpbit.EmgDataRecorder;
import edu.crimpbit.RecordingService;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ResourceBundle;

/**
 * Created by Andy Moncsek on 26.06.14.
 *
 * @author Andy Moncsek
 */
@Fragment(id = BasicConfig.RECORDER_FRAGMENT,
        viewLocation = "/fxml/RecorderFragment.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        scope = Scope.PROTOTYPE)
public class RecorderFragment {

    @Resource
    private Context context;

    @Resource
    private ResourceBundle bundle;

    @FXML
    private ToggleButton recordButton;

    @FXML
    private LineChart<Integer, Byte> emgChart;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ConnectorService connectorService;

    private EmgDataRecorder recorder;

    private int emgDataOffset;

    private ObservableList<LineChart.Series<Integer, Byte>> emgData = FXCollections.observableArrayList();
    private Timeline chartUpdateTimeline;

    @FXML
    private void initialize() {
        for (int i = 0; i < 8; ++i) {
            XYChart.Series<Integer, Byte> series = new LineChart.Series<>();
            series.setName("EMG " + i);
            emgData.add(series);
        }
        emgChart.setData(emgData);
        recordButton.selectedProperty().addListener(observable -> {
            ObservableList<Device> devices = connectorService.getDevices();
            if (recordButton.isSelected()) {
                recorder = recordingService.startRecording(devices.get(0));
                recorder.awaitRunning();
                recordButton.setText(bundle.getString("recorder.recording"));
                ObservableList<EmgDataRecorder.EmgDataRecord> records = recorder.getRecords();
                chartUpdateTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                    int emgDataSize = records.size();
                    for (int i = emgDataOffset; i < emgDataSize; ++i) {
                        EmgDataRecorder.EmgDataRecord record = records.get(i);
                        for (int emgIndex = 0; emgIndex < 8; ++emgIndex) {
                            emgData.get(emgIndex).getData().add(new XYChart.Data<Integer, Byte>(i, record.getData()[emgIndex]));
                        }
                        emgDataOffset = i;
                    }
                }));
                chartUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
                chartUpdateTimeline.play();
            } else {
                recorder.stopAsync().awaitTerminated();
                recordButton.setText(bundle.getString("recorder.record"));
                chartUpdateTimeline.stop();
            }
        });
    }

}
