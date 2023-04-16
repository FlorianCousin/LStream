package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class LinearCollectorsTest {

  @Test
  void toCollection() {

    TreeSet<String> actualCollection =
        LinearStream.of("a", "b", "d", "c").collect(LinearCollectors.toCollection(TreeSet::new));

    assertThat(actualCollection)
        .isExactlyInstanceOf(TreeSet.class)
        .containsExactly("a", "b", "c", "d");
  }

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
