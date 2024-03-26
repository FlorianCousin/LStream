package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import org.junit.jupiter.api.Test;

class SummarizingCollectorTest {

  @Test
  void summarizingIntEmpty() {

    IntSummaryStatistics actualStatistics =
        LStream.<Integer>empty().collect(LCollectors.summarizingInt(Integer::intValue));

    // TODO Add comparator of summarizing in assertJ ?
    assertThat(actualStatistics.getCount()).isZero();
    assertThat(actualStatistics.getMin()).isEqualTo(Integer.MAX_VALUE);
    assertThat(actualStatistics.getMax()).isEqualTo(Integer.MIN_VALUE);
    assertThat(actualStatistics.getSum()).isZero();
  }

  @Test
  void summarizingIntOneElement() {

    IntSummaryStatistics actualStatistics =
        LStream.of("Long.MAX_VALUE").collect(LCollectors.summarizingInt(String::length));

    assertThat(actualStatistics.getCount()).isEqualTo(1);
    assertThat(actualStatistics.getMin()).isEqualTo(14);
    assertThat(actualStatistics.getMax()).isEqualTo(14);
    assertThat(actualStatistics.getSum()).isEqualTo(14);
  }

  @Test
  void summarizingIntElements() {

    IntSummaryStatistics actualStatistics =
        LStream.of("word", "Long", "2L").collect(LCollectors.summarizingInt(String::length));

    assertThat(actualStatistics.getCount()).isEqualTo(3);
    assertThat(actualStatistics.getMin()).isEqualTo(2);
    assertThat(actualStatistics.getMax()).isEqualTo(4);
    assertThat(actualStatistics.getSum()).isEqualTo(10);
  }

  @Test
  void summarizingLongEmpty() {

    LongSummaryStatistics actualStatistics =
        LStream.<Long>empty().collect(LCollectors.summarizingLong(Long::longValue));

    assertThat(actualStatistics.getCount()).isZero();
    assertThat(actualStatistics.getMin()).isEqualTo(Long.MAX_VALUE);
    assertThat(actualStatistics.getMax()).isEqualTo(Long.MIN_VALUE);
    assertThat(actualStatistics.getSum()).isZero();
  }

  @Test
  void summarizingLongOneElement() {

    LongSummaryStatistics actualStatistics =
        LStream.of(Integer.MAX_VALUE + 1L).collect(LCollectors.summarizingLong(Long::longValue));

    long expectedSingleValue = Integer.MAX_VALUE + 1L;

    assertThat(actualStatistics.getCount()).isEqualTo(1);
    assertThat(actualStatistics.getMin()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getMax()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getSum()).isEqualTo(expectedSingleValue);
  }

  @Test
  void summarizingLongElements() {

    LongSummaryStatistics actualStatistics =
        LStream.of("word", "Long", "2L")
            .collect(LCollectors.summarizingLong(s -> (long) Integer.MAX_VALUE + s.length()));

    assertThat(actualStatistics.getCount()).isEqualTo(3);
    assertThat(actualStatistics.getMin()).isEqualTo(Integer.MAX_VALUE + 2L);
    assertThat(actualStatistics.getMax()).isEqualTo(Integer.MAX_VALUE + 4L);
    assertThat(actualStatistics.getSum()).isEqualTo(3L * Integer.MAX_VALUE + 4L + 4L + 2L);
  }

  @Test
  void summarizingDoubleEmpty() {

    DoubleSummaryStatistics actualStatistics =
        LStream.<Double>empty().collect(LCollectors.summarizingDouble(Double::doubleValue));

    assertThat(actualStatistics.getCount()).isZero();
    assertThat(actualStatistics.getMin()).isEqualTo(Double.POSITIVE_INFINITY);
    assertThat(actualStatistics.getMax()).isEqualTo(Double.NEGATIVE_INFINITY);
    assertThat(actualStatistics.getSum()).isZero();
  }

  @Test
  void summarizingDoubleOneElement() {

    DoubleSummaryStatistics actualStatistics =
        LStream.of(Integer.MAX_VALUE + 0.5)
            .collect(LCollectors.summarizingDouble(Double::doubleValue));

    double expectedSingleValue = Integer.MAX_VALUE + 0.5;

    assertThat(actualStatistics.getCount()).isEqualTo(1);
    assertThat(actualStatistics.getMin()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getMax()).isEqualTo(expectedSingleValue);
    assertThat(actualStatistics.getSum()).isEqualTo(expectedSingleValue);
  }

  @Test
  void summarizingDoubleElements() {

    DoubleSummaryStatistics actualStatistics =
        LStream.of("check", "computers", "timer")
            .collect(LCollectors.summarizingDouble(s -> s.length() / 2.));

    assertThat(actualStatistics.getCount()).isEqualTo(3);
    assertThat(actualStatistics.getMin()).isEqualTo(2.5);
    assertThat(actualStatistics.getMax()).isEqualTo(4.5);
    assertThat(actualStatistics.getSum()).isEqualTo(9.5);
  }
}
