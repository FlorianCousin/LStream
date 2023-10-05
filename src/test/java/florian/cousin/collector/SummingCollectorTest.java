package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import florian.cousin.LStream;
import org.junit.jupiter.api.Test;

class SummingCollectorTest {

  @Test
  void summingIntEmpty() {

    int actualSum = LStream.<Integer>empty().collect(LCollectors.summingInt(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingIntOneElement() {

    int actualSum = LStream.of("hello !").collect(LCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(7);
  }

  @Test
  void summingIntElements() {

    int actualSum =
        LStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingLongEmpty() {

    long actualSum = LStream.<Integer>empty().collect(LCollectors.summingLong(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingLongOneElement() {

    long actualSum =
        LStream.of("hello !")
            .collect(
                LCollectors.summingLong(string -> ((long) Integer.MAX_VALUE) + string.length()));

    assertThat(actualSum).isEqualTo(2_147_483_654L);
  }

  @Test
  void summingLongElements() {

    long actualSum =
        LStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LCollectors.summingLong(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingDoubleEmpty() {

    double actualSum = LStream.<Integer>empty().collect(LCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleOneElement() {

    double actualSum =
        LStream.of("hello !")
            .collect(LCollectors.summingDouble(string -> (double) string.length() / 2));

    assertThat(actualSum).isEqualTo(3.5);
  }

  @Test
  void summingDoubleElements() {

    double actualSum =
        LStream.of(Math.sqrt(2), Math.sqrt(3), Math.sqrt(5))
            .collect(LCollectors.summingDouble(t -> t));

    assertThat(actualSum).isCloseTo(5.382332347441762, offset(1e-14));
  }

  @Test
  void summingDoubleWrongOrder() {

    double actualSum = LStream.of(1e300, 3.5, -1e300).collect(LCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleRightOrder() {

    double actualSum = LStream.of(1e300, -1e300, 3.5).collect(LCollectors.summingDouble(t -> t));

    assertThat(actualSum).isEqualTo(3.5);
  }
}
