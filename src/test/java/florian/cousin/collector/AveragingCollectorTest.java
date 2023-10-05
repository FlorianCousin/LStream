package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import florian.cousin.LinearStream;
import org.junit.jupiter.api.Test;

class AveragingCollectorTest {

  @Test
  void averagingIntEmpty() {

    double actualAverage =
        LinearStream.<Integer>empty().collect(LinearCollectors.averagingInt(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingIntOneElement() {

    double actualAverage =
        LinearStream.of("hello !").collect(LinearCollectors.averagingInt(String::length));

    assertThat(actualAverage).isEqualTo(7);
  }

  @Test
  void averagingIntElements() {

    double actualAverage = LinearStream.of(2, 3, 5).collect(LinearCollectors.averagingInt(t -> t));

    assertThat(actualAverage).isCloseTo(3.333333333333333, offset(1e-14));
  }

  @Test
  void averagingLongEmpty() {

    double actualAverage =
        LinearStream.<Integer>empty().collect(LinearCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingLongOneElement() {

    double actualAverage =
        LinearStream.of(Long.MAX_VALUE).collect(LinearCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  void averagingLongElements() {

    double actualAverage =
        LinearStream.of(Long.MAX_VALUE, Long.MIN_VALUE, 2L)
            .collect(LinearCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isCloseTo(0.333333333333333, offset(1e-14));
  }

  @Test
  void averagingDoubleEmpty() {

    double actualAverage =
        LinearStream.<Integer>empty().collect(LinearCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingDoubleOneElement() {

    double actualAverage =
        LinearStream.of("hello !")
            .collect(LinearCollectors.averagingDouble(string -> (double) string.length() / 2));

    assertThat(actualAverage).isEqualTo(3.5);
  }

  @Test
  void averagingDoubleElements() {

    double actualAverage =
        LinearStream.of(Math.sqrt(2), Math.sqrt(3), Math.sqrt(5))
            .collect(LinearCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isCloseTo(1.7941107824805875, offset(1e-14));
  }

  @Test
  void averagingDoubleWrongOrder() {

    double actualAverage =
        LinearStream.of(1e300, 3.5, -1e300).collect(LinearCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingDoubleRightOrder() {

    double actualSum =
        LinearStream.of(1e300, -1e300, 3d).collect(LinearCollectors.averagingDouble(t -> t));

    assertThat(actualSum).isCloseTo(1, offset(1e-14));
  }
}
