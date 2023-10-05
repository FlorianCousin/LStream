package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ToSetCollectorTest {

  @Test
  void toSetIsModifiable() {

    Set<Integer> actualCollection = LStream.of(4, 8, 6, 4, 6).collect(LCollectors.toSet());

    actualCollection.add(7);

    Set<Integer> expectedCollection = Set.of(4, 8, 6, 7);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void toUnmodifiableSet() {

    Set<Integer> actualCollection =
        LStream.of(4, 8, 6, 4, 6).collect(LCollectors.toUnmodifiableSet());

    Set<Integer> expectedCollection = Set.of(4, 8, 6);

    assertThat(actualCollection).isUnmodifiable().isEqualTo(expectedCollection);
  }
}
