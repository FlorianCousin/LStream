package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SortedLStreamTest {

  @Test
  void count() {

    long actualCount = LStream.from(Set.of(1, 9, 5)).sorted().count();

    Assertions.assertThat(actualCount).isEqualTo(3);
  }

  @Test
  void findOne() {

    Optional<Integer> actualOneFound = LStream.of(7).sorted().findOne();

    Assertions.assertThat(actualOneFound).hasValue(7);
  }
}
