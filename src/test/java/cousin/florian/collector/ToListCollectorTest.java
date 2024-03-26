package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class ToListCollectorTest {

  @Test
  void toListIsModifiable() {

    List<Integer> actualCollection = LStream.of(4, 8, 6, 4, 6).collect(LCollectors.toList());

    actualCollection.add(7);

    List<Integer> expectedCollection = List.of(4, 8, 6, 4, 6, 7);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void toUnmodifiableList() {

    List<Integer> actualCollection =
        LStream.of(4, 8, 6, 4, 6).collect(LCollectors.toUnmodifiableList());

    List<Integer> expectedCollection = List.of(4, 8, 6, 4, 6);

    assertThat(actualCollection).isUnmodifiable().isEqualTo(expectedCollection);
  }
}
