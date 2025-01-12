package cousin.florian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import cousin.florian.collector.LCollector;
import cousin.florian.exception.SeveralElementsException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class LStreamEndTest {

  @Test
  void nothingExecutedWithoutCollection() {
    LStream.of(1, 2, 8, -4).map(number -> fail("mapping function should not be called"));
  }

  @Test
  void forEach() {

    AtomicInteger nb = new AtomicInteger(0);

    LStream.of(1, -2, 4).forEach(nb::addAndGet);

    assertThat(nb).hasValue(3);
  }

  @Test
  void toArray() {

    Object[] actualValues = LStream.of("a", "b", "c", "d").toArray();

    Object[] expectedValues = new Object[] {"a", "b", "c", "d"};

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void toArrayGenerator() {

    String[] actualValues = LStream.of("a", "b", "c", "d").toArray(String[]::new);

    String[] expectedValues = new String[] {"a", "b", "c", "d"};

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void maxWithReduce() {

    int actualMaximum = LStream.of(1, 5, 9, 3, -14, 6, 753, 0).reduce(0, Math::max);

    int expectedMaximum = 753;

    assertThat(actualMaximum).isEqualTo(expectedMaximum);
  }

  @Test
  void reduceWithoutValue() {

    Optional<Object> optional = LStream.empty().reduce((a, b) -> a);

    assertThat(optional).isEmpty();
  }

  @Test
  void reduceOneValue() {

    Optional<Integer> actualMin = LStream.of(5).reduce(Math::min);

    assertThat(actualMin).hasValue(5);
  }

  @Test
  void reduceSeveralValues() {

    Optional<Integer> actualMax = LStream.of(4, 8, 9, -1, 3).reduce(Math::max);

    assertThat(actualMax).hasValue(9);
  }

  @Test
  void reduceSum() {

    Optional<Integer> actualMax = LStream.of(4, 8, 9, -1, 3).reduce(Integer::sum);

    assertThat(actualMax).hasValue(23);
  }

  @Test
  void reduceWithNullAccumulatorResult() {

    Optional<String> actualReduction =
        LStream.of("4", "8", "9", "-1", "3")
            .reduce(
                (accumulation, newValue) -> "9".equals(newValue) ? null : accumulation + newValue);

    assertThat(actualReduction).hasValue("null-13");
  }

  @Test
  void reduceWithDifferentType() {

    String actualMaximum =
        LStream.of(1, 5, 9, 3, -14, 6, 753, 0)
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

    Set<Integer> actualCollection = LStream.of(4, 5, 4, 3).collect(HashSet::new, Set::add);

    Set<Integer> expectedCollection = Set.of(3, 4, 5);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void collectWithCollector() {

    LCollector<Integer, List<Integer>, List<Integer>> integerListCollector =
        LCollector.of(ArrayList::new, List::add, Collections::unmodifiableList);

    List<Integer> actualCollection = LStream.of(4, 5, 4, 3).collect(integerListCollector);

    List<Integer> expectedCollection = List.of(4, 5, 4, 3);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }

  @Test
  void minWithoutValue() {

    Optional<Integer> actualMinimum = LStream.<Integer>empty().min(Comparator.naturalOrder());

    assertThat(actualMinimum).isEmpty();
  }

  @Test
  void minWithOneValue() {

    Optional<String> actualMinimum = LStream.of("g").min(Comparator.naturalOrder());

    assertThat(actualMinimum).hasValue("g");
  }

  @Test
  void minWithSeveralValues() {

    Optional<Integer> actualMinPositive =
        LStream.of(4, 9, -7, -2, 3)
            .min(
                Comparator.<Integer, Boolean>comparing(number -> number < 0)
                    .thenComparing(Comparator.naturalOrder()));

    assertThat(actualMinPositive).hasValue(3);
  }

  @Test
  void maxWithoutValue() {

    Optional<Integer> actualMaximum = LStream.<Integer>empty().max(Comparator.naturalOrder());

    assertThat(actualMaximum).isEmpty();
  }

  @Test
  void maxWithOneValue() {

    Optional<String> actualMaximum = LStream.of("g").max(Comparator.naturalOrder());

    assertThat(actualMaximum).hasValue("g");
  }

  @Test
  void maxWithSeveralValues() {

    Optional<Integer> actualMaxNegative =
        LStream.of(4, 9, -7, -2, 3)
            .max(
                Comparator.<Integer, Boolean>comparing(number -> number < 0)
                    .thenComparing(Comparator.naturalOrder()));

    assertThat(actualMaxNegative).hasValue(-2);
  }

  @Test
  void countNothing() {

    long actualNbElements = LStream.empty().count();

    assertThat(actualNbElements).isZero();
  }

  @Test
  void count() {

    long actualNbStartsWithF =
        LStream.of("756f", "f", "fdu3il", "54df", "fdo")
            .filter(characters -> characters.startsWith("f"))
            .count();

    assertThat(actualNbStartsWithF).isEqualTo(3);
  }

  @Test
  void anyMatchNoElements() {

    boolean actualHasNull = LStream.empty().anyMatch(Objects::isNull);

    assertThat(actualHasNull).isFalse();
  }

  @Test
  void anyMatchEarlyReturn() {

    boolean actualHasNegative = LStream.of(4, -12, -753, 56).anyMatch(i -> i < 0);

    assertThat(actualHasNegative).isTrue();
  }

  @Test
  void anyMatchUntilTheEnd() {

    boolean actualContainsBruno =
        LStream.of("cold", "heart", "Elton", "John", "Remix").anyMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isFalse();
  }

  @Test
  void allMatchNoElements() {

    boolean actualHasNull = LStream.empty().allMatch(Objects::isNull);

    assertThat(actualHasNull).isTrue();
  }

  @Test
  void allMatchUntilTheEnd() {

    boolean actualHasNegative = LStream.of(-4, -12, -753, -56).allMatch(i -> i < 0);

    assertThat(actualHasNegative).isTrue();
  }

  @Test
  void allMatchEarlyReturn() {

    boolean actualContainsBruno =
        LStream.of("Bruno", "heart", "Elton", "John", "Remix").allMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isFalse();
  }

  @Test
  void noneMatchNoElements() {

    boolean actualHasNull = LStream.empty().noneMatch(Objects::isNull);

    assertThat(actualHasNull).isTrue();
  }

  @Test
  void noneMatchEarlyReturn() {

    boolean actualHasNegative = LStream.of(4, -12, -753, 56).noneMatch(i -> i < 0);

    assertThat(actualHasNegative).isFalse();
  }

  @Test
  void noneMatchUntilTheEnd() {

    boolean actualContainsBruno =
        LStream.of("cold", "heart", "Elton", "John", "Remix").noneMatch("Bruno"::equals);

    assertThat(actualContainsBruno).isTrue();
  }

  @Test
  void findFirstNoElements() {

    Optional<Object> actualFirst = LStream.empty().findFirst();

    assertThat(actualFirst).isEmpty();
  }

  @Test
  void findFirst() {

    Optional<String> actualFirst =
        LStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("S"))
            .findFirst();

    assertThat(actualFirst).hasValue("Sweet");
  }

  @Test
  void findFirstIsNull() {

    Optional<String> actualFirst = LStream.of(null, "5", "d").findFirst();

    assertThat(actualFirst).isEmpty();
  }

  @Test
  void findOneNoElements() {

    Optional<Object> actualOne = LStream.empty().findOne();

    assertThat(actualOne).isEmpty();
  }

  @Test
  void findOneElement() {

    Optional<String> actualOne =
        LStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("T"))
            .findOne();

    assertThat(actualOne).hasValue("The");
  }

  @Test
  void findOneSeveralElements() {

    ThrowableAssert.ThrowingCallable findOneSeveralElements =
        () ->
            LStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
                .filter(s -> s.startsWith("S"))
                .findOne();

    Assertions.assertThatThrownBy(findOneSeveralElements)
        .isInstanceOf(SeveralElementsException.class)
        .hasMessage("Call to \"findOne\" but there were several elements left in the stream");
  }

  @Test
  void findOneIsNull() {

    Optional<String> actualOne =
        LStream.of(null, "5", "d").filter(s -> s == null || s.endsWith("a")).findOne();

    assertThat(actualOne).isEmpty();
  }

  @Test
  void findLastNoElements() {

    Optional<Object> actualLast = LStream.empty().findLast();

    assertThat(actualLast).isEmpty();
  }

  @Test
  void findLast() {

    Optional<String> actualLast =
        LStream.of("Bitter", "Sweet", "Symphony", "The", "Verve")
            .filter(s -> s.startsWith("S"))
            .findLast();

    assertThat(actualLast).hasValue("Symphony");
  }

  @Test
  void findLastIsNull() {

    Optional<String> actualLast = LStream.of("null", "5", "d", null).findLast();

    assertThat(actualLast).isEmpty();
  }

  @Test
  void iterator() {

    LStream<String> baseLStream = LStream.of("null", "5", "d", null);
    LStream<String> actualIterator = baseLStream.iterator();

    assertThat(actualIterator).isSameAs(baseLStream);
  }

  @Test
  void spliterator() {

    List<String> baseElements = Arrays.asList("null", "5", "d", null);
    Spliterator<String> actualSpliterator = LStream.from(baseElements).spliterator();

    Iterator<String> expectedElements = baseElements.iterator();
    actualSpliterator.forEachRemaining(
        actualElement -> assertThat(actualElement).isEqualTo(expectedElements.next()));
    assertFalse(expectedElements.hasNext());
  }
}
