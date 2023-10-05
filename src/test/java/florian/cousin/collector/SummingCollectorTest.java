package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import florian.cousin.LinearStream;
import org.junit.jupiter.api.Test;

class SummingCollectorTest {

  @Test
  void summingIntEmpty() {

    int actualSum = LinearStream.<Integer>empty().collect(LinearCollectors.summingInt(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingIntOneElement() {

    int actualSum = LinearStream.of("hello !").collect(LinearCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(7);
  }

  @Test
  void summingIntElements() {

    int actualSum =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingLongEmpty() {

    long actualSum = LinearStream.<Integer>empty().collect(LinearCollectors.summingLong(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingLongOneElement() {

    long actualSum =
        LinearStream.of("hello !")
            .collect(
                LinearCollectors.summingLong(
                    string -> ((long) Integer.MAX_VALUE) + string.length()));

    assertThat(actualSum).isEqualTo(2_147_483_654L);
  }

  @Test
  void summingLongElements() {

    long actualSum =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.summingLong(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingDoubleEmpty() {

    double actualSum =
        LinearStream.<Integer>empty().collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleOneElement() {

    double actualSum =
        LinearStream.of("hello !")
            .collect(LinearCollectors.summingDouble(string -> (double) string.length() / 2));

    assertThat(actualSum).isEqualTo(3.5);
  }

  @Test
  void summingDoubleElements() {

    double actualSum =
        LinearStream.of(Math.sqrt(2), Math.sqrt(3), Math.sqrt(5))
            .collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isCloseTo(5.382332347441762, offset(1e-14));
  }

  @Test
  void summingDoubleWrongOrder() {

    double actualSum =
        LinearStream.of(1e300, 3.5, -1e300).collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleRightOrder() {

    double actualSum =
        LinearStream.of(1e300, -1e300, 3.5).collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isEqualTo(3.5);
  }
}
