package florian.cousin.collector;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import florian.cousin.LinearStream;
import java.util.*;
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

  @Test
  void flatMapping() {

    List<String> letters = List.of("a", "b", "c", "d");
    String actualFlatMapping =
        LinearStream.of(1, 0, 3)
            .collect(
                LinearCollectors.flatMapping(
                    nb -> LinearStream.from(nCopies(nb, letters.get(nb))),
                    LinearCollectors.joining(" ")));

    String expectedFlatMapping = "b d d d";

    assertThat(actualFlatMapping).isEqualTo(expectedFlatMapping);
  }

  @Test
  void filtering() {

    List<Integer> actualFiltered =
        LinearStream.of(-1, 6, 0, -2, 7, 1, 6)
            .collect(LinearCollectors.filtering(nb -> nb > 0, LinearCollectors.toList()));

    List<Integer> expectedFiltered = List.of(6, 7, 1, 6);

    assertThat(actualFiltered).isEqualTo(expectedFiltered);
  }

  @Test
  void collectingAndThenAfterPreviousFinisher() {

    int actualCollectionAndThen =
        LinearStream.of("0", "1", "3")
            .collect(
                LinearCollectors.collectingAndThen(
                    LinearCollectors.joining("2"), Integer::parseInt));

    int expectedCollectingAndThen = 2123;

    assertThat(actualCollectionAndThen).isEqualTo(expectedCollectingAndThen);
  }

  @Test
  void collectingAndThenAddFinisher() {

    List<String> actualCollectionAndThen =
        LinearStream.of("0", "1", "3")
            .collect(
                LinearCollectors.collectingAndThen(
                    LinearCollectors.toList(), Collections::unmodifiableList));

    List<String> expectedCollectingAndThen = List.of("0", "1", "3");

    assertThat(actualCollectionAndThen).isUnmodifiable().isEqualTo(expectedCollectingAndThen);
  }

  @Test
  void countingNothing() {

    long actualNbElements = LinearStream.empty().collect(LinearCollectors.counting());

    long expectedNbElements = 0;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }

  @Test
  void countingElements() {

    long actualNbElements = LinearStream.of(1, 8, 0).collect(LinearCollectors.counting());

    long expectedNbElements = 3;

    assertThat(actualNbElements).isEqualTo(expectedNbElements);
  }

  @Test
  void minByEmpty() {

    Optional<Integer> actualMin =
        LinearStream.<Integer>empty()
            .collect(LinearCollectors.minBy(Comparator.<Integer>naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void minByOneElement() {

    Optional<String> actualMin =
        LinearStream.of("b").collect(LinearCollectors.minBy(Comparator.<String>naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void minByElements() {

    Optional<String> actualMin =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.minBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Thomas");
  }

  @Test
  void maxByEmpty() {

    Optional<Integer> actualMin =
        LinearStream.<Integer>empty()
            .collect(LinearCollectors.maxBy(Comparator.<Integer>naturalOrder()));

    assertThat(actualMin).isEmpty();
  }

  @Test
  void maxByOneElement() {

    Optional<String> actualMin =
        LinearStream.of("b").collect(LinearCollectors.maxBy(Comparator.<String>naturalOrder()));

    assertThat(actualMin).hasValue("b");
  }

  @Test
  void maxByElements() {

    Optional<String> actualMin =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.maxBy(Comparator.comparing(s -> s.substring(3))));

    assertThat(actualMin).hasValue("Florian");
  }

  @Test
  void summingIntEmpty() {

    int actualSum = LinearStream.<Integer>empty().collect(LinearCollectors.summingInt(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingIntOneElement() {

    int actualSum = LinearStream.of("hello !").collect(LinearCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(7);
  }

  @Test
  void summingIntElements() {

    int actualSum =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.summingInt(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingLongEmpty() {

    long actualSum = LinearStream.<Integer>empty().collect(LinearCollectors.summingLong(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingLongOneElement() {

    long actualSum =
        LinearStream.of("hello !")
            .collect(
                LinearCollectors.summingLong(
                    string -> ((long) Integer.MAX_VALUE) + string.length()));

    assertThat(actualSum).isEqualTo(2_147_483_654L);
  }

  @Test
  void summingLongElements() {

    long actualSum =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(LinearCollectors.summingLong(String::length));

    assertThat(actualSum).isEqualTo(37);
  }

  @Test
  void summingDoubleEmpty() {

    double actualSum =
        LinearStream.<Integer>empty().collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleOneElement() {

    double actualSum =
        LinearStream.of("hello !")
            .collect(LinearCollectors.summingDouble(string -> (double) string.length() / 2));

    assertThat(actualSum).isEqualTo(3.5);
  }

  @Test
  void summingDoubleElements() {

    double actualSum =
        LinearStream.of(Math.sqrt(2), Math.sqrt(3), Math.sqrt(5))
            .collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isCloseTo(5.382332347441762, offset(1e-14));
  }

  @Test
  void summingDoubleWrongOrder() {

    double actualSum =
        LinearStream.of(1e300, 3.5, -1e300).collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isZero();
  }

  @Test
  void summingDoubleRightOrder() {

    double actualSum =
        LinearStream.of(1e300, -1e300, 3.5).collect(LinearCollectors.summingDouble(t -> t));

    assertThat(actualSum).isEqualTo(3.5);
  }
}
