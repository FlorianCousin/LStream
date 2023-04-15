package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.*;
import org.junit.jupiter.api.Test;

class LinearStreamNullValuesTest {

  @Test
  void filter() {

    List<Integer> actualNullValues = LinearStream.of(1, null, 6).filter(Objects::isNull).toList();

    assertThat(actualNullValues).containsExactlyElementsOf(Collections.singleton(null));
  }

  @Test
  void map() {

    List<Integer> actualValues =
        LinearStream.of(null, 2).map(number -> number == null ? 3 : null).toList();

    List<Integer> expectedValues = Arrays.asList(3, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void flatMap() {

    List<Integer> actualValues =
        LinearStream.of(null, List.of(4, 6))
            .flatMap(value -> value == null ? LinearStream.of(7, 9) : LinearStream.from(value))
            .toList();

    List<Integer> expectedValues = List.of(7, 9, 4, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void flatMapperShouldNotReturnNull() {

    LinearStream<Object> mappedToNull = LinearStream.of(7).flatMap(value -> null);

    assertThatThrownBy(mappedToNull::toList).isExactlyInstanceOf(NullPointerException.class);
  }

  @Test
  void distinct() {

    List<Integer> actualDistinctValues = LinearStream.of(4, 8, null, 8, null).distinct().toList();

    assertThat(actualDistinctValues).containsExactlyInAnyOrder(4, 8, null);
  }

  @Test
  void sorted() {

    List<Integer> actualSortedValues =
        LinearStream.of(7, null, 6)
            .sorted(Comparator.nullsFirst(Comparator.naturalOrder()))
            .toList();

    List<Integer> expectedSortedValues = Arrays.asList(null, 6, 7);

    assertThat(actualSortedValues).isEqualTo(expectedSortedValues);
  }

  @Test
  void peek() {

    List<Integer> actualValues = LinearStream.of(8, null).peek(System.out::println).toList();

    List<Integer> expectedValues = Arrays.asList(8, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void limit() {

    List<Integer> actualValues = LinearStream.of(4, null, null, 6).limit(2).toList();

    List<Integer> expectedValues = Arrays.asList(4, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void skip() {

    List<Integer> actualValues = LinearStream.of(4, null, null, 6).skip(2).toList();

    List<Integer> expectedValues = Arrays.asList(null, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhileNotNull() {

    List<Integer> actualValues =
        LinearStream.of(1, 7, null, 9).takeWhile(Objects::nonNull).toList();

    List<Integer> expectedValues = List.of(1, 7);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhileNull() {

    List<Integer> actualValues =
        LinearStream.of(null, null, 8, null).takeWhile(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhilePreviousNotNull() {

    List<Integer> actualValues =
        LinearStream.of(1, 7, null, 9, null).takeWhilePrevious(Objects::nonNull).toList();

    List<Integer> expectedValues = Arrays.asList(1, 7, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhilePreviousNull() {

    List<Integer> actualValues =
        LinearStream.of(null, null, 8, null, 7, 5).takeWhilePrevious(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, null, 8);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileNull() {

    List<Integer> actualValues =
        LinearStream.of(null, null, 8, null, 7).dropWhile(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(8, null, 7);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileNotNull() {

    List<Integer> actualValues =
        LinearStream.of(1, 3, null, 8, null, 6).dropWhile(Objects::nonNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, 8, null, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }
}
