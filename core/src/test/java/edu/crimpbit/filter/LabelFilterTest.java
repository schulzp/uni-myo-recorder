package edu.crimpbit.filter;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Dario on 19.12.2015.
 */
public class LabelFilterTest {

    @Test
    public void labelRecords1() {
        LabelFilter labelFilter = new LabelFilter(4);
        Stream<Byte> stream = labelFilter.apply(Stream.of(new Byte[]{-128, -90, 0, 78, 127}));
        assertEquals(stream.collect(Collectors.toList()), Stream.of(new Byte[]{1, 1, 3, 4, 4}).collect(Collectors.toList()));
    }

    @Test
    public void labelRecords2() {
        LabelFilter labelFilter = new LabelFilter(1);
        Stream<Byte> stream = labelFilter.apply(Stream.of(new Byte[]{-128, -90, 0, 78, 127}));
        assertEquals(stream.collect(Collectors.toList()), Stream.of(new Byte[]{1, 1, 1, 1, 1}).collect(Collectors.toList()));
    }

}
