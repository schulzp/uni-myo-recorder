package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("prototype")
public class RecordingChartsFragment {

    private static final int MAX_OVERRIDES = 3;
    private static final int EMG_SENSOR_COUNT = 8;
    private static final int CHART_UPDATE_CHUNK_SIZE = 20;
    private static final int LIVE_UPDATE_RATE = 10;

    private final List<List<LineChart.Series<Number, Number>>> emgData = new ArrayList<>(EMG_SENSOR_COUNT);

    @FXML
    private TilePane emgCharts;

    private int numberOfOverrides;

    private int layerIndex;

    private List<Recording.EmgRecord> records;

    private Timeline liveUpdate;

    public void setRecords(List<Recording.EmgRecord> records) {
        this.records = records;

        for (int i = 0; i < records.size(); ++i) {
            addRecord(i);
        }

        layerIndex = numberOfOverrides++ % MAX_OVERRIDES;
    }

    public void startLiveUpdate() {
        liveUpdate = new Timeline(new KeyFrame(Duration.millis(LIVE_UPDATE_RATE), new EventHandler<ActionEvent>() {

            private int emgDataRangeBegin = 0;
            private int emgDataRangeEnd = 0;

            private int a;

            @Override
            public void handle(ActionEvent event) {
                emgDataRangeEnd += records.size();
                if (emgDataRangeEnd - emgDataRangeBegin > CHART_UPDATE_CHUNK_SIZE) {
                    int begin = emgDataRangeBegin;
                    int end = emgDataRangeEnd;
                    Platform.runLater(() -> {
                        for (int emgDataOffset = begin; emgDataOffset < end; ++emgDataOffset) {
                            addRecord(emgDataOffset);
                        }
                    });
                    emgDataRangeBegin = emgDataRangeEnd;
                }
            }

        }));
        liveUpdate.setCycleCount(Timeline.INDEFINITE);
        liveUpdate.play();
    }

    public void stopLiveUpdate() {
        liveUpdate.stop();
    }

    private void addRecord(int recordIndex) {
        Recording.EmgRecord record = records.get(recordIndex);
        for (int emgIndex = 0; emgIndex < EMG_SENSOR_COUNT; ++emgIndex) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(recordIndex, filter(record.getData()[emgIndex]));
            emgData.get(emgIndex).get(layerIndex).getData().add(data);
        }
    }

    private int filter(byte b) {
        return b;
    }

    @FXML
    private void initialize() {
        createCharts();
    }

    private void createCharts() {
        for (int emgIndex = 0; emgIndex < EMG_SENSOR_COUNT; ++emgIndex) {
            ObservableList layerSeries = FXCollections.observableArrayList();
            for (int layerIndex = 0; layerIndex < MAX_OVERRIDES; ++layerIndex) {
                XYChart.Series<Number, Number> series = new LineChart.Series<>();
                series.setName("Layer " + layerIndex);
                layerSeries.add(series);
            }

            emgData.add(layerSeries);

            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis(-150, 150, 50);
            yAxis.setAutoRanging(false);

            LineChart<Number, Number> emgChart = new LineChart<>(xAxis,  yAxis);
            emgChart.setAnimated(false);
            emgChart.setMaxSize(300, 150);
            emgChart.setTitle("EMG " + emgIndex);
            emgChart.setTitleSide(Side.TOP);
            emgChart.getStyleClass().add("emg-chart");
            emgChart.setData(layerSeries);
            emgChart.applyCss();

            emgCharts.getChildren().add(emgChart);
        }
    }

}
