package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MinByCollectorTest {

  @Test
  void minByEmpty() {

    Optional<Integer> actualMin =
        LStream.<Integer>empty().collect(LCollectors.minBy(Comparator.naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void minByOneElement() {

    Optional<String> actualMin =
        LStream.of("b").collect(LCollectors.minBy(Comparator.naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void minByElements() {

    Optional<String> actualMin =
        LStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LCollectors.minBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Thomas");
  }
}
