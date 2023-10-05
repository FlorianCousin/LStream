package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import org.junit.jupiter.api.Test;

class CountingCollectorTest {

  @Test
  void countingNothing() {

    long actualNbElements = LStream.empty().collect(LCollectors.counting());

    long expectedNbElements = 0;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }

  @Test
  void countingElements() {

    long actualNbElements = LStream.of(1, 8, 0).collect(LCollectors.counting());

    long expectedNbElements = 3;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }
}
