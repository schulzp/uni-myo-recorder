package edu.crimpbit.filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by peter on 15/12/15.
 */
public class AvgEmgFilter {

    private int chunkSize = 5;

    public List<int[]> apply(List<int[]> data) {
        List<int[]> result = new LinkedList<>();
        for (int begin = 0, end, size; begin < data.size(); begin = end) {
            end = Math.min(begin + chunkSize, data.size());
            final int offset = begin + (end - begin) / 2;
            data.subList(begin, end).stream()
                    .mapToInt(point -> point[1])
                    .average()
                    .ifPresent(average -> {
                        result.add(new int[]{offset, (int) average});
                    });
        }
        return result;
    }

}
