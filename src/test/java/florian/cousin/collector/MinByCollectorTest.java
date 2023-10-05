package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MinByCollectorTest {

  @Test
  void minByEmpty() {

    Optional<Integer> actualMin =
        LinearStream.<Integer>empty().collect(LinearCollectors.minBy(Comparator.naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void minByOneElement() {

    Optional<String> actualMin =
        LinearStream.of("b").collect(LinearCollectors.minBy(Comparator.naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void minByElements() {

    Optional<String> actualMin =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.minBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Thomas");
  }
}
