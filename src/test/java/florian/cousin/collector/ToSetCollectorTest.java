package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ToSetCollectorTest {

  @Test
  void toSetIsModifiable() {

    Set<Integer> actualCollection =
        LinearStream.of(4, 8, 6, 4, 6).collect(LinearCollectors.toSet());

    actualCollection.add(7);

    Set<Integer> expectedCollection = Set.of(4, 8, 6, 7);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void toUnmodifiableSet() {

    Set<Integer> actualCollection =
        LinearStream.of(4, 8, 6, 4, 6).collect(LinearCollectors.toUnmodifiableSet());

    Set<Integer> expectedCollection = Set.of(4, 8, 6);

    assertThat(actualCollection).isUnmodifiable().isEqualTo(expectedCollection);
  }
}
