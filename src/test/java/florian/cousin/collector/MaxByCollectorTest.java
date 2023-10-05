package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.Comparator;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MaxByCollectorTest {

  @Test
  void maxByEmpty() {

    Optional<Integer> actualMin =
        LinearStream.<Integer>empty().collect(LinearCollectors.maxBy(Comparator.naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void maxByOneElement() {

    Optional<String> actualMin =
        LinearStream.of("b").collect(LinearCollectors.maxBy(Comparator.naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void maxByElements() {

    Optional<String> actualMin =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.maxBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Florian");
  }
}
