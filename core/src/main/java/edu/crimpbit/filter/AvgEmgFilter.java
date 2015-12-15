package edu.crimpbit.filter;

import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by peter on 15/12/15.
 */
public class AvgEmgFilter implements EmgFilter {

    private int chunkSize = 5;

    public List<XYChart.Data<Integer, Integer>> apply(List<XYChart.Data<Integer, Integer>> data) {
        List<XYChart.Data<Integer, Integer>> result = new LinkedList<>();
        for (int begin = 0, end, size; begin < data.size(); begin = end) {
            end = Math.min(begin + chunkSize, data.size());
            final int offset = begin + (end - begin) / 2;
            data.subList(begin, end).stream()
                    .mapToInt(XYChart.Data::getXValue)
                    .average()
                    .ifPresent(average -> {
                        result.add(new XYChart.Data<>(offset, (int) average));
                    });
            ;
        }
        return result;
    }

}
