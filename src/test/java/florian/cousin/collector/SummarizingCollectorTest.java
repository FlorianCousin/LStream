package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import org.junit.jupiter.api.Test;

class SummarizingCollectorTest {

  @Test
  void summarizingIntEmpty() {

    IntSummaryStatistics actualStatistics =
        LinearStream.<Integer>empty().collect(LinearCollectors.summarizingInt(Integer::intValue));

    assertThat(actualStatistics.getCount()).isZero();
    assertThat(actualStatistics.getMin()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actualStatistics.getMax()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actualStatistics.getSum()).isZero();
  }

  @Test
  void summarizingIntOneElement() {

    IntSummaryStatistics actualStatistics =
        LinearStream.of("Long.MAX_VALUE").collect(LinearCollectors.summarizingInt(String::length));

    assertThat(actualStatistics.getCount()).isEqualTo(1);
    assertThat(actualStatistics.getMin()).isEqualTo(14);
    assertThat(actualStatistics.getMax()).isEqualTo(14);
    assertThat(actualStatistics.getSum()).isEqualTo(14);
  }

  @Test
  void summarizingIntElements() {

    IntSummaryStatistics actualStatistics =
        LinearStream.of("word", "Long", "2L")
            .collect(LinearCollectors.summarizingInt(String::length));

    assertThat(actualStatistics.getCount()).isEqualTo(3);
    assertThat(actualStatistics.getMin()).isEqualTo(2);
    assertThat(actualStatistics.getMax()).isEqualTo(4);
    assertThat(actualStatistics.getSum()).isEqualTo(10);
  }

  @Test
  void summarizingLongEmpty() {

    LongSummaryStatistics actualStatistics =
        LinearStream.<Long>empty().collect(LinearCollectors.summarizingLong(Long::longValue));

    assertThat(actualStatistics.getCount()).isZero();
    assertThat(actualStatistics.getMin()).isEqualTo(Long.MAX_VALUE);
    assertThat(actualStatistics.getMax()).isEqualTo(Long.MIN_VALUE);
    assertThat(actualStatistics.getSum()).isZero();
  }

  @Test
  void summarizingLongOneElement() {

    LongSummaryStatistics actualStatistics =
        LinearStream.of(Integer.MAX_VALUE + 1L)
            .collect(LinearCollectors.summarizingLong(Long::longValue));

    long expectedSingleValue = Integer.MAX_VALUE + 1L;

    assertThat(actualStatistics.getCount()).isEqualTo(1);
    assertThat(actualStatistics.getMin()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getMax()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getSum()).isEqualTo(expectedSingleValue);
  }

  @Test
  void summarizingLongElements() {

    LongSummaryStatistics actualStatistics =
        LinearStream.of("word", "Long", "2L")
            .collect(LinearCollectors.summarizingLong(s -> (long) Integer.MAX_VALUE + s.length()));

    assertThat(actualStatistics.getCount()).isEqualTo(3);
    assertThat(actualStatistics.getMin()).isEqualTo(Integer.MAX_VALUE + 2L);
    assertThat(actualStatistics.getMax()).isEqualTo(Integer.MAX_VALUE + 4L);
    assertThat(actualStatistics.getSum()).isEqualTo(3L * Integer.MAX_VALUE + 4L + 4L + 2L);
  }
}
