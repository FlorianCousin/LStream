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

  @Test
  void joiningSimple() {

    String actualJoin = LinearStream.of("j", "o", "i", "n").collect(LinearCollectors.joining());

    String expectedJoin = "join";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningEmpty() {

    String actualJoin = LinearStream.<String>empty().collect(LinearCollectors.joining());

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningOneValue() {

    String actualJoin = LinearStream.of("j").collect(LinearCollectors.joining());

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterSimple() {

    String actualJoin = LinearStream.of("j", "o", "i", "n").collect(LinearCollectors.joining("d"));

    String expectedJoin = "jdodidn";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterEmpty() {

    String actualJoin = LinearStream.<String>empty().collect(LinearCollectors.joining("d"));

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterOneValue() {

    String actualJoin = LinearStream.of("j").collect(LinearCollectors.joining("d"));

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixSimple() {

    String actualJoin =
        LinearStream.of("j", "o", "i", "n")
            .collect(LinearCollectors.joining("d", "prefix", "suffix"));

    String expectedJoin = "prefixjdodidnsuffix";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixEmpty() {

    String actualJoin =
        LinearStream.<String>empty().collect(LinearCollectors.joining("d", "p", "s"));

    String expectedJoin = "ps";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixOneValue() {

    String actualJoin =
        LinearStream.of("j").collect(LinearCollectors.joining("d", "pre : \"", "\", end"));

    String expectedJoin = "pre : \"j\", end";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void mappingEmpty() {

    String actualMapping =
        LinearStream.empty()
            .collect(LinearCollectors.mapping(s -> s + "a", LinearCollectors.joining()));

    String expectedMapping = "";

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }

  @Test
  void mappingNumbers() {

    List<Integer> actualMapping =
        LinearStream.of(0, 1, 2, 3, 4, 5)
            .collect(LinearCollectors.mapping(i -> i * i, LinearCollectors.toList()));

    List<Integer> expectedMapping = List.of(0, 1, 4, 9, 16, 25);

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }
}
