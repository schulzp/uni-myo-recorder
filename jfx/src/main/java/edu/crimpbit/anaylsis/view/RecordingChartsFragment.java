package edu.crimpbit.anaylsis.view;

import edu.crimpbit.EMGData;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;
import one.util.streamex.EntryStream;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

@Controller
@Scope("prototype")
public class RecordingChartsFragment {

    private static final int CHART_UPDATE_CHUNK_SIZE = 20;
    private static final int LIVE_UPDATE_RATE = 50;

    private final List<List<LineChart.Series<Number, Number>>> emgChartData = new ArrayList<>(EMGData.NUM_EMG_PADS);

    @FXML
    private TilePane emgCharts;

    @FXML
    private ComboBox<Supplier<UnaryOperator<EntryStream<Integer, Double>>>> emgFilters;

    private int seriesIndex = -1;

    private EMGData emgData;

    private Timeline liveUpdate;

    public void addEmgData(EMGData emgData) {
        seriesIndex++;
        this.emgData = emgData;
        addRecords(0, emgData.size());
    }

    public void startLiveUpdate() {
        liveUpdate = new Timeline(new KeyFrame(Duration.millis(LIVE_UPDATE_RATE), new EventHandler<ActionEvent>() {

            private int emgDataRangeBegin = 0;
            private int emgDataRangeEnd = 0;

            private int a;

            @Override
            public void handle(ActionEvent event) {
                emgDataRangeEnd = emgData.size();
                if (emgDataRangeEnd - emgDataRangeBegin > CHART_UPDATE_CHUNK_SIZE) {
                    int begin = emgDataRangeBegin;
                    int end = emgDataRangeEnd;
                    Platform.runLater(() -> addRecords(begin, end));
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

    @FXML
    private void initialize() {
        createCharts();
        createFilters();
    }

    private void addRecords(int begin, int end) {
        System.err.println("adding " + begin + " - " + end + " of " + emgData.size());
        Supplier<XYChart.Series<Number, Number>> seriesSupplier = () -> createSeries("Data");

        emgData.stream().parallel()
                .mapValues(rawData -> EntryStream.of(rawData).skip(begin).map(entry -> createChartData(entry)).toList())
                .mapKeyValue((chartIndex, seriesData) -> createChartUpdater(chartIndex, seriesIndex, seriesData, seriesSupplier))
                .forEach(Platform::runLater);
    }

    private Runnable createChartUpdater(int chartIndex, int seriesIndex, List<XYChart.Data<Number, Number>> seriesData, Supplier<XYChart.Series<Number, Number>> seriesSupplier) {
        return () -> getSeries(chartIndex, seriesIndex, seriesSupplier).getData().addAll(seriesData);
    }

    private void createFilters() {
        emgFilters.getItems().addAll(createFilterSupplier("Envelope", () -> new EnvelopeFollowerFilter(0.3, 0.8)));
        emgFilters.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> applyFilter(newValue)));
    }

    private <I, O, F extends  Function<I, O>> Supplier<F> createFilterSupplier(String name, Supplier<F> supplier) {
        return new Supplier<F>() {

            @Override
            public F get() {
                return supplier.get();
            }

            @Override
            public String toString() {
                return name;
            }

        };
    }

    private void applyFilter(Supplier<UnaryOperator<EntryStream<Integer, Double>>> filterSupplier) {
        seriesIndex++;
        Supplier<XYChart.Series<Number, Number>> seriesSupplier = () -> createSeries("Filter " + filterSupplier);
        emgData.stream().parallel()
                .mapValues(rawData -> {
                    UnaryOperator<EntryStream<Integer, Double>> filter = filterSupplier.get();
                    return filter.apply(EntryStream.of(rawData).mapToValue((i, v) -> v.doubleValue())).map(entry -> createChartData(entry)).toList();
                })
                .mapKeyValue((chartIndex, seriesData) -> createChartUpdater(chartIndex, seriesIndex, seriesData, seriesSupplier))
                .forEach(Platform::runLater);
    }

    private XYChart.Data<Number, Number> createChartData(Map.Entry<? extends Number, ? extends Number> entry) {
        return new XYChart.Data<>(entry.getKey(), entry.getValue());
    }

    private XYChart.Series<Number, Number> getSeries(int padIndex, int seriesIndex, Supplier<XYChart.Series<Number, Number>> supplier) {
        List<XYChart.Series<Number, Number>> padSeries = emgChartData.get(padIndex);
        if (seriesIndex == padSeries.size()) {
            padSeries.add(supplier.get());
        }
        return padSeries.get(seriesIndex);
    }

    private XYChart.Series<Number, Number> createSeries(String name) {
        XYChart.Series<Number, Number> series = new LineChart.Series<>();
        series.setName(name);
        return series;
    }

    private void createCharts() {
        IntStream.range(0, EMGData.NUM_EMG_PADS)
                .mapToObj(emgPadIndex -> createChart("EMG " + emgPadIndex))
                .forEach(emgCharts.getChildren()::add);
    }

    private LineChart<Number, Number> createChart(String title) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(-150, 150, 50);
        yAxis.setAutoRanging(false);

        LineChart<Number, Number> emgChart = new LineChart<>(xAxis,  yAxis);
        emgChart.setAnimated(false);
        emgChart.setMaxSize(350, 150);
        emgChart.setTitle(title);
        emgChart.setTitleSide(Side.TOP);
        emgChart.getStyleClass().add("emg-chart");
        emgChart.setData(createChartData());
        emgChart.applyCss();
        return emgChart;
    }

    private ObservableList createChartData() {
        ObservableList layerSeries = FXCollections.observableArrayList();
        emgChartData.add(layerSeries);
        return layerSeries;
    }

}
