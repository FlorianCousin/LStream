package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class LStreamNullValuesTest {

  @Test
  void filter() {

    List<Integer> actualNullValues = LStream.of(1, null, 6).filter(Objects::isNull).toList();

    assertThat(actualNullValues).containsExactlyElementsOf(Collections.singleton(null));
  }

  @Test
  void map() {

    List<Integer> actualValues =
        LStream.of(null, 2).map(number -> number == null ? 3 : null).toList();

    List<Integer> expectedValues = Arrays.asList(3, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void flatMap() {

    List<Integer> actualValues =
        LStream.of(null, List.of(4, 6))
            .flatMap(value -> value == null ? LStream.of(7, 9) : LStream.from(value))
            .toList();

    List<Integer> expectedValues = List.of(7, 9, 4, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void flatMapperCanReturnNull() {

    List<Integer> actualValues =
        LStream.of(7, 5)
            .flatMap(value -> value == 7 ? null : LStream.iterate(value, i -> i + 1).limit(2))
            .toList();

    List<Integer> expectedValues = List.of(5, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void distinct() {

    List<Integer> actualDistinctValues = LStream.of(4, 8, null, 8, null).distinct().toList();

    assertThat(actualDistinctValues).containsExactlyInAnyOrder(4, 8, null);
  }

  @Test
  void sorted() {

    List<Integer> actualSortedValues =
        LStream.of(7, null, 6).sorted(Comparator.nullsFirst(Comparator.naturalOrder())).toList();

    List<Integer> expectedSortedValues = Arrays.asList(null, 6, 7);

    assertThat(actualSortedValues).isEqualTo(expectedSortedValues);
  }

  @Test
  void peek() {

    List<Integer> actualValues = LStream.of(8, null).peek(System.out::println).toList();

    List<Integer> expectedValues = Arrays.asList(8, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void limit() {

    List<Integer> actualValues = LStream.of(4, null, null, 6).limit(2).toList();

    List<Integer> expectedValues = Arrays.asList(4, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void skip() {

    List<Integer> actualValues = LStream.of(4, null, null, 6).skip(2).toList();

    List<Integer> expectedValues = Arrays.asList(null, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void skipArrayLBigLong() {

    List<Integer> actualValues = LStream.of(4, null, null, 6).skip(Integer.MAX_VALUE + 2L).toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void skipNegative() {

    ThrowableAssert.ThrowingCallable skipper = () -> LStream.generate(() -> 3).limit(5).skip(-1);

    assertThatThrownBy(skipper).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void skipNegativeArrayL() {

    ThrowableAssert.ThrowingCallable skipper = () -> LStream.of(4, null, null, 6).skip(-1);

    assertThatThrownBy(skipper).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void skipNegativeEmptyL() {

    ThrowableAssert.ThrowingCallable skipper = () -> LStream.<Integer>empty().skip(-1);

    assertThatThrownBy(skipper).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void takeWhileNotNull() {

    List<Integer> actualValues = LStream.of(1, 7, null, 9).takeWhile(Objects::nonNull).toList();

    List<Integer> expectedValues = List.of(1, 7);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhileNull() {

    List<Integer> actualValues =
        LStream.of(null, null, 8, null).takeWhile(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhilePreviousNotNull() {

    List<Integer> actualValues =
        LStream.of(1, 7, null, 9, null).takeWhilePrevious(Objects::nonNull).toList();

    List<Integer> expectedValues = Arrays.asList(1, 7, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void takeWhilePreviousNull() {

    List<Integer> actualValues =
        LStream.of(null, null, 8, null, 7, 5).takeWhilePrevious(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, null, 8);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileNull() {

    List<Integer> actualValues =
        LStream.of(null, null, 8, null, 7).dropWhile(Objects::isNull).toList();

    List<Integer> expectedValues = Arrays.asList(8, null, 7);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void dropWhileNotNull() {

    List<Integer> actualValues =
        LStream.of(1, 3, null, 8, null, 6).dropWhile(Objects::nonNull).toList();

    List<Integer> expectedValues = Arrays.asList(null, 8, null, 6);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void reduceSameType() {

    Integer actualMaxString =
        LStream.of(1, null, 3, null)
            .reduce(
                0,
                (previousValue, nextValue) ->
                    String.valueOf(previousValue).compareTo(String.valueOf(nextValue)) < 0
                        ? nextValue
                        : previousValue);

    assertThat(actualMaxString).isNull();
  }

  @Test
  void reduceDifferentType() {

    String actualMaxString =
        LStream.of(1, null, 3, null)
            .reduce(
                "0",
                (previousValue, nextValue) ->
                    Collections.max(List.of(previousValue, String.valueOf(nextValue))));

    assertThat(actualMaxString).isEqualTo("null");
  }

  @Test
  void reduceWithoutIdentity() {

    Optional<Integer> actualMaxString =
        LStream.of(1, null, 3, null)
            .reduce(
                (previousValue, nextValue) ->
                    String.valueOf(previousValue).compareTo(String.valueOf(nextValue)) < 0
                        ? nextValue
                        : previousValue);

    assertThat(actualMaxString).isEmpty();
  }

  @Test
  void ofAndToList() {

    List<Integer> actualValues = LStream.of(null, 5, null).toList();

    List<Integer> expectedValues = Arrays.asList(null, 5, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void count() {

    long actualValues = LStream.of(null, 5, null).count();

    assertThat(actualValues).isEqualTo(3);
  }

  @Test
  void builder() {

    List<Integer> actualValues = LStream.<Integer>builder().add(5).add(null).build().toList();

    List<Integer> expectedValues = Arrays.asList(5, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void from() {

    List<Integer> actualValues = LStream.from(Arrays.asList(null, 4, null)).toList();

    List<Integer> expectedValues = Arrays.asList(null, 4, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void iterate() {

    List<String> actualValues =
        LStream.iterate("soap", previousValue -> previousValue == null ? "soap" : null)
            .limit(5)
            .toList();

    List<String> expectedValues = Arrays.asList("soap", null, "soap", null, "soap");

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void generate() {

    AtomicBoolean previousIsNull = new AtomicBoolean(false);

    List<String> actualValues =
        LStream.generate(() -> previousIsNull.getAndSet(!previousIsNull.get()) ? null : "word")
            .limit(5)
            .toList();

    List<String> expectedValues = Arrays.asList("word", null, "word", null, "word");

    assertThat(actualValues).isEqualTo(expectedValues);
  }
}
