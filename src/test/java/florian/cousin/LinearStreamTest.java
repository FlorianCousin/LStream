package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
}
