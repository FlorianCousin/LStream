package cousin.florian;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class LStreamModificationTest {

  @Test
  void filter() {

    List<Float> actualFiltered =
        LStream.of(4.5f, 7.8f, -4f, 0.0003f, -7.1f).filter(number -> number < 0).toList();

    List<Float> expectedFiltered = List.of(-4f, -7.1f);

    assertThat(actualFiltered)
        .withFailMessage("filter function has not been called")
        .isEqualTo(expectedFiltered);
  }

  @Test
  void map() {

    List<Integer> actualLengths =
        LStream.of("bonjour", "hi", "hello", "hola").map(String::length).toList();

    List<Integer> expectedLengths = List.of(7, 2, 5, 4);

    assertThat(actualLengths)
        .withFailMessage("mapping function has not been called")
        .isEqualTo(expectedLengths);
  }

  @Test
  void flatMap() {

    List<Character> actualFlatMap =
        LStream.of("cola", "moli", "dude", "flatMap")
            .map(word -> word.chars().mapToObj(c -> (char) c).toList())
            .flatMap(LStream::from)
            .toList();

    List<Character> expectedFlatMap =
        List.of(
            'c', 'o', 'l', 'a', 'm', 'o', 'l', 'i', 'd', 'u', 'd', 'e', 'f', 'l', 'a', 't', 'M',
            'a', 'p');

    assertThat(actualFlatMap).isEqualTo(expectedFlatMap);
  }

  @Test
  void flatMapLastOneEmpty() {

    List<Integer> actualNumbers =
        LStream.of(List.of(1, -7, 6), Collections.<Integer>emptyList())
            .flatMap(LStream::from)
            .toList();

    List<Integer> expectedNumbers = List.of(1, -7, 6);

    assertThat(actualNumbers).isEqualTo(expectedNumbers);
  }

  @Test
  void distinct() {

    List<String> actualDistinct =
        LStream.of("me", "me", "you", "it", "him", "me", "it").distinct().toList();

    List<String> expectedDistinct = List.of("me", "you", "it", "him");

    assertThat(actualDistinct).isEqualTo(expectedDistinct);
  }

  @Test
  void sortedNatural() {

    List<Integer> descendingSortedIntegers =
        IntStream.range(0, 9).map(i -> 12 - i).boxed().toList();

    List<Integer> actualSortedIntegers = LStream.from(descendingSortedIntegers).sorted().toList();

    List<Integer> expectedSortedIntegers = List.of(4, 5, 6, 7, 8, 9, 10, 11, 12);

    assertThat(actualSortedIntegers).isEqualTo(expectedSortedIntegers);
  }

  @Test
  void sortedComparator() {

    List<String> actualSortedWords =
        LStream.of("sort", "those", "words")
            .sorted(
                Comparator.<String, Character>comparing(word -> word.charAt(1))
                    .thenComparing(word -> word.charAt(0)))
            .toList();

    List<String> expectedSortedWords = List.of("those", "sort", "words");

    assertThat(actualSortedWords).isEqualTo(expectedSortedWords);
  }

  @Test
  void sortedMustKeepDuplicate() {

    List<Integer> actualValues = LStream.of(4, 9, 3, 4, 6, 3, 4, 4).sorted().toList();

    List<Integer> expectedValues = List.of(3, 3, 4, 4, 4, 4, 6, 9);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void peek() {

    AtomicInteger number = new AtomicInteger(0);

    LStream.of(1, 6, 1).peek(i -> number.incrementAndGet()).toList();

    assertThat(number).hasValue(3);
  }

  @Test
  void limitToNothing() {

    List<String> actualValues = LStream.of("yes", "si", "oui", "da").limit(0).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void limit() {

    List<String> actualValues = LStream.of("yes", "si", "oui", "da").limit(2).toList();

    List<String> expectedValues = List.of("yes", "si");

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void limitToAll() {

    List<String> baseValues = List.of("yes", "si", "oui", "da");

    List<String> actualValues = LStream.from(baseValues).limit(20).toList();

    assertThat(actualValues).isEqualTo(baseValues);
  }

  @Test
  void skipNothing() {

    List<Long> baseValues = List.of(4L, -5L, 789L);

    List<Long> actuelValues = LStream.from(baseValues).skip(0).toList();

    assertThat(actuelValues).isEqualTo(baseValues);
  }

  @Test
  void skip() {

    List<Long> actuelValues = LStream.of(4L, -5L, 789L).skip(2).toList();

    assertThat(actuelValues).hasSize(1).containsExactly(789L);
  }

  @Test
  void skipAll() {

    List<Long> actuelValues = LStream.of(4L, -5L, 789L).skip(4).toList();

    assertThat(actuelValues).isEmpty();
  }

  @Test
  void takeWhileTakesNothing() {

    List<Integer> actualValues = LStream.of(1, 4, 8, 3).takeWhile(i -> false).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void takeWhile() {

    List<Integer> actualValues =
        LStream.of(-1, -5, 25, 7, -4, 1, 3).takeWhile(nb -> nb < 0).toList();

    List<Integer> expectedValues = List.of(-1, -5);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhileTakesEverything() {

    List<Integer> baseValues = List.of(4, 5, -9);

    List<Integer> actualValues = LStream.from(baseValues).takeWhile(nb -> true).toList();

    assertThat(actualValues).isEqualTo(baseValues);
  }

  @Test
  void takeWhileEmpty() {

    List<Integer> actualValues = LStream.<Integer>empty().takeWhilePrevious(i -> i > 0).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void takeWhilePrevious() {

    List<String> actualValues =
        LStream.of("linear", "lap", "crowd", "log")
            .takeWhilePrevious(s -> s.startsWith("l"))
            .toList();

    List<String> expectedValues = List.of("linear", "lap", "crowd");

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhilePreviousUseless() {

    List<Integer> actualValues = LStream.of(4, 8, 6, 3).takeWhilePrevious(i -> i > 0).toList();

    List<Integer> expectedValues = List.of(4, 8, 6, 3);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileTakesNothing() {

    List<Integer> actualValues = LStream.of(1, 6, 7).dropWhile(i -> true).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void dropWhile() {

    List<Integer> actualValues =
        LStream.of(1, 8, -1, -2, 75, 6, -3, 6).dropWhile(nb -> nb > 0).toList();

    List<Integer> expectedValues = List.of(-1, -2, 75, 6, -3, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileTakesEverything() {

    List<Integer> actualValues = LStream.of(5, 9, 6).dropWhile(i -> false).toList();

    List<Integer> expectedValues = List.of(5, 9, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }
}
