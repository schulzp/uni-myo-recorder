package edu.crimpbit.filter;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;

/**
 * Created by peter on 15/12/15.
 */
public class AvgEmgFilterTest {

    private AvgEmgFilter filter = new AvgEmgFilter();

    @Test
    public void applyWithSingleItem() {
        List<int[]> data = Collections.singletonList(new int[]{0, 0});
        List<int[]> result = filter.apply(data);

        assertThat(result, ListOfIntArraysMatcher.equalTo(result));
    }

    @Test
    public void applyWithMultipleItem() {
        List<int[]> data = Arrays.asList(new int[]{0, 0}, new int[]{2, 2});

        assertThat(filter.apply(data), ListOfIntArraysMatcher.equalTo(Collections.singletonList(new int[]{1, 2})));
    }

    public static class ListOfIntArraysMatcher extends BaseMatcher<List<int[]>> {

        private final List<int[]> expected;

        public ListOfIntArraysMatcher(List<int[]> expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object item) {
            boolean matches = true;
            List<int[]> actual = (List<int[]>) item;
            for (int i = 0; i < actual.size(); ++i) {
                if (!Arrays.equals(expected.get(i), actual.get(i))) {
                    matches = false;
                }
            }
            return matches;
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            List<int[]> actual = (List<int[]>) item;
            description.appendText("was:  ")
                    .appendValueList("", ",", "", actual.stream().map(Arrays::toString).collect(Collectors.toList()));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("list: ")
                    .appendValueList("", ",", "", expected.stream().map(Arrays::toString).collect(Collectors.toList()));
        }

        public static ListOfIntArraysMatcher equalTo(List<int[]> expected) {
            return new ListOfIntArraysMatcher(expected);
        }

    }

}