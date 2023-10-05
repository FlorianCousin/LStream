package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class ToListCollectorTest {

  @Test
  void toListIsModifiable() {

    List<Integer> actualCollection =
        LinearStream.of(4, 8, 6, 4, 6).collect(LinearCollectors.toList());

    actualCollection.add(7);

    List<Integer> expectedCollection = List.of(4, 8, 6, 4, 6, 7);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void toUnmodifiableList() {

    List<Integer> actualCollection =
            LinearStream.of(4, 8, 6, 4, 6).collect(LinearCollectors.toUnmodifiableList());

    List<Integer> expectedCollection = List.of(4, 8, 6, 4, 6);

    assertThat(actualCollection).isUnmodifiable().isEqualTo(expectedCollection);
  }
}
