package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import org.junit.jupiter.api.Test;

class CountingCollectorTest {

  @Test
  void countingNothing() {

    long actualNbElements = LinearStream.empty().collect(LinearCollectors.counting());

    long expectedNbElements = 0;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }

  @Test
  void countingElements() {

    long actualNbElements = LinearStream.of(1, 8, 0).collect(LinearCollectors.counting());

    long expectedNbElements = 3;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }
}
