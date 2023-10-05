package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MaxByCollectorTest {

  @Test
  void maxByEmpty() {

    Optional<Integer> actualMin =
        LStream.<Integer>empty().collect(LCollectors.maxBy(Comparator.naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void maxByOneElement() {

    Optional<String> actualMin =
        LStream.of("b").collect(LCollectors.maxBy(Comparator.naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void maxByElements() {

    Optional<String> actualMin =
        LStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LCollectors.maxBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Florian");
  }
}
