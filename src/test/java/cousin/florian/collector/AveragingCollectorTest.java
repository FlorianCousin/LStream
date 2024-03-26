package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import cousin.florian.LStream;
import org.junit.jupiter.api.Test;

class AveragingCollectorTest {

  @Test
  void averagingIntEmpty() {

    double actualAverage = LStream.<Integer>empty().collect(LCollectors.averagingInt(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingIntOneElement() {

    double actualAverage = LStream.of("hello !").collect(LCollectors.averagingInt(String::length));

    assertThat(actualAverage).isEqualTo(7);
  }

  @Test
  void averagingIntElements() {

    double actualAverage = LStream.of(2, 3, 5).collect(LCollectors.averagingInt(t -> t));

    assertThat(actualAverage).isCloseTo(3.333333333333333, offset(1e-14));
  }

  @Test
  void averagingLongEmpty() {

    double actualAverage = LStream.<Integer>empty().collect(LCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingLongOneElement() {

    double actualAverage = LStream.of(Long.MAX_VALUE).collect(LCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  void averagingLongElements() {

    double actualAverage =
        LStream.of(Long.MAX_VALUE, Long.MIN_VALUE, 2L).collect(LCollectors.averagingLong(t -> t));

    assertThat(actualAverage).isCloseTo(0.333333333333333, offset(1e-14));
  }

  @Test
  void averagingDoubleEmpty() {

    double actualAverage = LStream.<Integer>empty().collect(LCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingDoubleOneElement() {

    double actualAverage =
        LStream.of("hello !")
            .collect(LCollectors.averagingDouble(string -> (double) string.length() / 2));

    assertThat(actualAverage).isEqualTo(3.5);
  }

  @Test
  void averagingDoubleElements() {

    double actualAverage =
        LStream.of(Math.sqrt(2), Math.sqrt(3), Math.sqrt(5))
            .collect(LCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isCloseTo(1.7941107824805875, offset(1e-14));
  }

  @Test
  void averagingDoubleWrongOrder() {

    double actualAverage =
        LStream.of(1e300, 3.5, -1e300).collect(LCollectors.averagingDouble(t -> t));

    assertThat(actualAverage).isZero();
  }

  @Test
  void averagingDoubleRightOrder() {

    double actualSum = LStream.of(1e300, -1e300, 3d).collect(LCollectors.averagingDouble(t -> t));

    assertThat(actualSum).isCloseTo(1, offset(1e-14));
  }
}
