package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import florian.cousin.collector.CollectorFinisher;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class LinearStreamTest {

  @Test
  void nothingExecutedWithoutCollection() {
    LinearStream.of(1, 2, 8, -4).map(number -> fail("mapping function should not be called"));
  }

  @Test
  void filter() {

    List<Float> actualFiltered =
        LinearStream.of(4.5f, 7.8f, -4f, 0.0003f, -7.1f).filter(number -> number < 0).toList();

    List<Float> expectedFiltered = List.of(-4f, -7.1f);

    assertThat(actualFiltered)
        .withFailMessage("filter function has not been called")
        .isEqualTo(expectedFiltered);
  }

  @Test
  void map() {

    List<Integer> actualLengths =
        LinearStream.of("bonjour", "hi", "hello", "hola").map(String::length).toList();

    List<Integer> expectedLengths = List.of(7, 2, 5, 4);

    assertThat(actualLengths)
        .withFailMessage("mapping function has not been called")
        .isEqualTo(expectedLengths);
  }

  @Test
  void flatMap() {

    List<Character> actualFlatMap =
        LinearStream.of("cola", "moli", "dude", "flatMap")
            .map(word -> word.chars().mapToObj(c -> (char) c).toList())
            .flatMap(LinearStream::from)
            .toList();

    List<Character> expectedFlatMap =
        List.of(
            'c', 'o', 'l', 'a', 'm', 'o', 'l', 'i', 'd', 'u', 'd', 'e', 'f', 'l', 'a', 't', 'M',
            'a', 'p');

    assertThat(actualFlatMap).isEqualTo(expectedFlatMap);
  }

  @Test
  void distinct() {

    List<String> actualDistinct =
        LinearStream.of("me", "me", "you", "it", "him", "me", "it").distinct().toList();

    List<String> expectedDistinct = List.of("me", "you", "it", "him");

    assertThat(actualDistinct).isEqualTo(expectedDistinct);
  }

  @Test
  void sortedNatural() {

    List<Integer> descendingSortedIntegers =
        IntStream.range(0, 9).map(i -> 12 - i).boxed().toList();

    List<Integer> actualSortedIntegers =
        LinearStream.from(descendingSortedIntegers).sorted().toList();

    List<Integer> expectedSortedIntegers = List.of(4, 5, 6, 7, 8, 9, 10, 11, 12);

    assertThat(actualSortedIntegers).isEqualTo(expectedSortedIntegers);
  }

  @Test
  void sortedComparator() {

    List<String> actualSortedWords =
        LinearStream.of("sort", "those", "words")
            .sorted(
                Comparator.<String, Character>comparing(word -> word.charAt(1))
                    .thenComparing(word -> word.charAt(0)))
            .toList();

    List<String> expectedSortedWords = List.of("those", "sort", "words");

    assertThat(actualSortedWords).isEqualTo(expectedSortedWords);
  }

  @Test
  void peek() {

    AtomicInteger number = new AtomicInteger(0);

    LinearStream.of(1, 6, 1).peek(i -> number.incrementAndGet()).toList();

    assertThat(number).hasValue(3);
  }

  @Test
  void limitToNothing() {

    List<String> actualValues = LinearStream.of("yes", "si", "oui", "da").limit(0).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void limit() {

    List<String> actualValues = LinearStream.of("yes", "si", "oui", "da").limit(2).toList();

    List<String> expectedValues = List.of("yes", "si");

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void limitToAll() {

    List<String> baseValues = List.of("yes", "si", "oui", "da");

    List<String> actualValues = LinearStream.from(baseValues).limit(20).toList();

    assertThat(actualValues).isEqualTo(baseValues);
  }

  @Test
  void skipNothing() {

    List<Long> baseValues = List.of(4L, -5L, 789L);

    List<Long> actuelValues = LinearStream.from(baseValues).skip(-1).toList();

    assertThat(actuelValues).isEqualTo(baseValues);
  }

  @Test
  void skip() {

    List<Long> actuelValues = LinearStream.of(4L, -5L, 789L).skip(2).toList();

    assertThat(actuelValues).hasSize(1).containsExactly(789L);
  }

  @Test
  void skipAll() {

    List<Long> actuelValues = LinearStream.of(4L, -5L, 789L).skip(4).toList();

    assertThat(actuelValues).isEmpty();
  }

  @Test
  void takeWhileTakesNothing() {

    List<Integer> actualValues = LinearStream.of(1, 4, 8, 3).takeWhile(i -> false).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void takeWhile() {

    List<Integer> actualValues =
        LinearStream.of(-1, -5, 25, 7, -4, 1, 3).takeWhile(nb -> nb < 0).toList();

    List<Integer> expectedValues = List.of(-1, -5);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhileTakesEverything() {

    List<Integer> baseValues = List.of(4, 5, -9);

    List<Integer> actualValues = LinearStream.from(baseValues).takeWhile(nb -> true).toList();

    assertThat(actualValues).isEqualTo(baseValues);
  }

  @Test
  void dropWhileTakesNothing() {

    List<Integer> actualValues = LinearStream.of(1, 6, 7).dropWhile(i -> true).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void dropWhile() {

    List<Integer> actualValues =
        LinearStream.of(1, 8, -1, -2, 75, 6, -3, 6).dropWhile(nb -> nb > 0).toList();

    List<Integer> expectedValues = List.of(-1, -2, 75, 6, -3, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileTakesEverything() {

    List<Integer> actualValues = LinearStream.of(5, 9, 6).dropWhile(i -> false).toList();

    List<Integer> expectedValues = List.of(5, 9, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
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

    CollectorFinisher<Integer, List<Integer>, List<Integer>> integerListCollector =
        new CollectorFinisher<>(ArrayList::new, List::add, Collections::unmodifiableList);

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
        LinearStream.of("756f", "f", "fduil", "54df", "fdo")
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
}
