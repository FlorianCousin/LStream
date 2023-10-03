package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import florian.cousin.collector.LinearCollector;
import florian.cousin.exception.SeveralElementsException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class LinearStreamEndTest {

  @Test
  void nothingExecutedWithoutCollection() {
    LinearStream.of(1, 2, 8, -4).map(number -> fail("mapping function should not be called"));
  }

  @Test
  void forEach() {

    AtomicInteger nb = new AtomicInteger(0);

    LinearStream.of(1, -2, 4).forEach(nb::addAndGet);

    assertThat(nb).hasValue(3);
  }

  @Test
  void toArray() {

    Object[] actualValues = LinearStream.of("a", "b", "c", "d").toArray();

    Object[] expectedValues = new Object[] {"a", "b", "c", "d"};

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void toArrayGenerator() {

    String[] actualValues = LinearStream.of("a", "b", "c", "d").toArray(String[]::new);

    String[] expectedValues = new String[] {"a", "b", "c", "d"};

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void maxWithReduce() {

    int actualMaximum = LinearStream.of(1, 5, 9, 3, -14, 6, 753, 0).reduce(0, Math::max);

    int expectedMaximum = 753;

    assertThat(actualMaximum).isEqualTo(expectedMaximum);
  }

  @Test
  void reduceWithoutValue() {

    Optional<Object> optional = LinearStream.empty().reduce((a, b) -> a);

    assertThat(optional).isEmpty();
  }

  @Test
  void reduceOneValue() {

    Optional<Integer> actualMin = LinearStream.of(5).reduce(Math::min);

    assertThat(actualMin).hasValue(5);
  }

  @Test
  void reduceSeveralValues() {

    Optional<Integer> actualMax = LinearStream.of(4, 8, 9, -1, 3).reduce(Math::max);

    assertThat(actualMax).hasValue(9);
  }

  @Test
  void reduceWithDifferentType() {

    String actualMaximum =
        LinearStream.of(1, 5, 9, 3, -14, 6, 753, 0)
            .reduce(
                "",
                (String previousMax, Integer newNumber) -> {
                  String stringNewNumber = String.valueOf(newNumber);
                  return previousMax.compareTo(stringNewNumber) < 0 ? stringNewNumber : previousMax;
                });

    String expectedMaximum = "9";

    assertThat(actualMaximum).isEqualTo(expectedMaximum);
  }

  @Test
  void collectWithAccumulator() {

    Set<Integer> actualCollection = LinearStream.of(4, 5, 4, 3).collect(HashSet::new, Set::add);

    Set<Integer> expectedCollection = Set.of(3, 4, 5);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void collectWithCollector() {

    LinearCollector<Integer, List<Integer>, List<Integer>> integerListCollector =
        LinearCollector.of(ArrayList::new, List::add, Collections::unmodifiableList);

    List<Integer> actualCollection = LinearStream.of(4, 5, 4, 3).collect(integerListCollector);

    List<Integer> expectedCollection = List.of(4, 5, 4, 3);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void minWithoutValue() {

    Optional<Integer> actualMinimum = LinearStream.<Integer>empty().min(Comparator.naturalOrder());

    assertThat(actualMinimum).isEmpty();
  }

  @Test
  void minWithOneValue() {

    Optional<String> actualMinimum = LinearStream.of("g").min(Comparator.naturalOrder());

    assertThat(actualMinimum).hasValue("g");
  }

  @Test
  void minWithSeveralValues() {

    Optional<Integer> actualMinPositive =
        LinearStream.of(4, 9, -7, -2, 3)
            .min(
                Comparator.<Integer, Boolean>comparing(number -> number < 0)
                    .thenComparing(Comparator.naturalOrder()));

    assertThat(actualMinPositive).hasValue(3);
  }

  @Test
  void maxWithoutValue() {

    Optional<Integer> actualMaximum = LinearStream.<Integer>empty().max(Comparator.naturalOrder());

    assertThat(actualMaximum).isEmpty();
  }

  @Test
  void maxWithOneValue() {

    Optional<String> actualMaximum = LinearStream.of("g").max(Comparator.naturalOrder());

    assertThat(actualMaximum).hasValue("g");
  }

  @Test
  void maxWithSeveralValues() {

    Optional<Integer> actualMaxNegative =
        LinearStream.of(4, 9, -7, -2, 3)
            .max(
                Comparator.<Integer, Boolean>comparing(number -> number < 0)
                    .thenComparing(Comparator.naturalOrder()));

    assertThat(actualMaxNegative).hasValue(-2);
  }

  @Test
  void countNothing() {

    long actualNbElements = LinearStream.empty().count();

    assertThat(actualNbElements).isZero();
  }

  @Test
  void count() {

    long actualNbStartsWithF =
        LinearStream.of("756f", "f", "fdu3il", "54df", "fdo")
            .filter(characters -> characters.startsWith("f"))
            .count();

    assertThat(actualNbStartsWithF).isEqualTo(3);
  }

  @Test
  void anyMatchNoElements() {

    boolean actualHasNull = LinearStream.empty().anyMatch(Objects::isNull);

    assertThat(actualHasNull).isFalse();
  }

  @Test
  void anyMatchEarlyReturn() {

    boolean actualHasNegative = LinearStream.of(4, -12, -753, 56).anyMatch(i -> i < 0);

    assertThat(actualHasNegative).isTrue();
  }

  @Test
  void anyMatchUntilTheEnd() {

    boolean actualContainsBruno =
        LinearStream.of("cold", "heart", "Elton", "John", "Remix").anyMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isFalse();
  }

  @Test
  void allMatchNoElements() {

    boolean actualHasNull = LinearStream.empty().allMatch(Objects::isNull);

    assertThat(actualHasNull).isTrue();
  }

  @Test
  void allMatchUntilTheEnd() {

    boolean actualHasNegative = LinearStream.of(-4, -12, -753, -56).allMatch(i -> i < 0);

    assertThat(actualHasNegative).isTrue();
  }

  @Test
  void allMatchEarlyReturn() {

    boolean actualContainsBruno =
        LinearStream.of("Bruno", "heart", "Elton", "John", "Remix").allMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isFalse();
  }

  @Test
  void noneMatchNoElements() {

    boolean actualHasNull = LinearStream.empty().noneMatch(Objects::isNull);

    assertThat(actualHasNull).isTrue();
  }

  @Test
  void noneMatchEarlyReturn() {

    boolean actualHasNegative = LinearStream.of(4, -12, -753, 56).noneMatch(i -> i < 0);

    assertThat(actualHasNegative).isFalse();
  }

  @Test
  void noneMatchUntilTheEnd() {

    boolean actualContainsBruno =
        LinearStream.of("cold", "heart", "Elton", "John", "Remix").noneMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isTrue();
  }

  @Test
  void findFirstNoElements() {

    Optional<Object> actualFirst = LinearStream.empty().findFirst();

    assertThat(actualFirst).isEmpty();
  }

  @Test
  void findFirst() {

    Optional<String> actualFirst =
        LinearStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("S"))
            .findFirst();

    assertThat(actualFirst).hasValue("Sweet");
  }

  @Test
  void findFirstIsNull() {

    Optional<String> actualFirst = LinearStream.of(null, "5", "d").findFirst();

    assertThat(actualFirst).isEmpty();
  }

  @Test
  void findOneNoElements() {

    Optional<Object> actualOne = LinearStream.empty().findOne();

    assertThat(actualOne).isEmpty();
  }

  @Test
  void findOneElement() {

    Optional<String> actualOne =
        LinearStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("T"))
            .findOne();

    assertThat(actualOne).hasValue("The");
  }

  @Test
  void findOneSeveralElements() {

    ThrowableAssert.ThrowingCallable findOneSeveralElements =
        () ->
            LinearStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
                .filter(s -> s.startsWith("S"))
                .findOne();

    Assertions.assertThatThrownBy(findOneSeveralElements)
        .isInstanceOf(SeveralElementsException.class)
        .hasMessage("Call to \"findOne\" but there were several elements left in the stream");
  }

  @Test
  void findOneIsNull() {

    Optional<String> actualOne =
        LinearStream.of(null, "5", "d").filter(s -> s == null || s.endsWith("a")).findOne();

    assertThat(actualOne).isEmpty();
  }

  @Test
  void findLastNoElements() {

    Optional<Object> actualLast = LinearStream.empty().findLast();

    assertThat(actualLast).isEmpty();
  }

  @Test
  void findLast() {

    Optional<String> actualLast =
        LinearStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("S"))
            .findLast();

    assertThat(actualLast).hasValue("Symphony");
  }

  @Test
  void findLastIsNull() {

    Optional<String> actualLast = LinearStream.of("null", "5", "d", null).findLast();

    assertThat(actualLast).isEmpty();
  }
}
