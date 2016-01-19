package edu.crimpbit.filter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by peter on 15/12/15.
 */
public class AverageFilterTest {


//    @Test
//    public void filterTest1() {
//        AverageFilter filter = new AverageFilter(1);
//        List<Byte> bytes = new ArrayList<>();
//        bytes.addAll(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5, 6, 7}));
//        Stream<Double> result = filter.apply(bytes.stream());
//        List<Double> averagesList = result.collect(Collectors.toList());
//        for (int i = 0; i < averagesList.size(); i++) {
//            assertTrue(averagesList.get(i) == bytes.get(i).doubleValue());
//        }
//    }
//
//    @Test
//    public void filterTest2() {
//
//        AverageFilter filter = new AverageFilter(2);
//        List<Byte> bytes = new ArrayList<>();
//        bytes.addAll(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8}));
//        List<Double> expectedResult = new ArrayList<>(Arrays.asList(new Double[]{0.5, 2.5, 4.5, 6.5, 8.0}));
//        Stream<Double> result = filter.apply(bytes.stream());
//        List<Double> averagesList = result.collect(Collectors.toList());
//        assertEquals(averagesList, expectedResult);
//    }
//
//    @Test
//    public void filterTest3() {
//        AverageFilter filter = new AverageFilter(9);
//        List<Byte> bytes = new ArrayList<>();
//        bytes.addAll(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8}));
//        Stream<Double> result = filter.apply(bytes.stream());
//        List<Double> averagesList = result.collect(Collectors.toList());
//        assertEquals(averagesList.get(0), bytes.stream().mapToDouble(Byte::doubleValue).average().getAsDouble());
//
//    }
//
//    @Test
//    public void filterTest4() {
//        AverageFilter filter = new AverageFilter(10);
//        List<Byte> bytes = new ArrayList<>();
//        bytes.addAll(Arrays.asList(new Byte[]{0, 1}));
//        Stream<Double> result = filter.apply(bytes.stream());
//        List<Double> averagesList = result.collect(Collectors.toList());
//        assertEquals(averagesList.get(0), bytes.stream().mapToDouble(Byte::doubleValue).average().getAsDouble());
//
//    }
}