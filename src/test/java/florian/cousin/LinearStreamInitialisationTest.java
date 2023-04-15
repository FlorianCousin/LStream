package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class LinearStreamInitialisationTest {

  @Test
  void builderEmpty() {

    List<Object> actualValues = LinearStream.builder().build().toList();

    assertThat(actualValues).isEmpty();
  }

  @Test
  void builderWithAdd() {

    List<String> actualValues =
        LinearStream.<String>builder()
            .add("first")
            .add("second")
            .add("third")
            .add("fourth")
            .build()
            .toList();

    List<String> expectedValues = List.of("first", "second", "third", "fourth");

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void builderWithAccept() {

    LinearStream.Builder<Long> builder = LinearStream.builder();
    builder.accept(5L);
    builder.accept(7L);
    builder.accept(3L);
    builder.accept(-5L);
    List<Long> actualValues = builder.build().toList();

    List<Long> expectedValues = List.of(5L, 7L, 3L, -5L);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void iterateInfinite() {

    List<Integer> actualNumbers = LinearStream.iterate(2, i -> (i * 5) % 47).limit(5).toList();

    List<Integer> expectedNumbers = List.of(2, 10, 3, 15, 28);

    assertThat(actualNumbers).isEqualTo(expectedNumbers);
  }

  @Test
  void iterateWithEndCondition() {

    List<Integer> actualValues = LinearStream.iterate(1, i -> i < 40, i -> (i * 5) % 47).toList();

    List<Integer> expectedValues = List.of(1, 5, 25, 31, 14, 23, 21, 11, 8, 40);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @Test
  void generateInfinite() {

    LinearStream<Integer> allPositiveNumbers = LinearStream.iterate(0, i -> i + 1);

    List<Integer> actualGeneratedNumbers =
        LinearStream.generate(allPositiveNumbers::next).limit(5).toList();

    List<Integer> expectedGeneratedNumbers = List.of(0, 1, 2, 3, 4);

    assertThat(actualGeneratedNumbers).isEqualTo(expectedGeneratedNumbers);
  }

  @Test
  void generateWithCondition() {

    LinearStream<Integer> numberSequence = LinearStream.iterate(3, i -> (i * 5) % 47);

    List<Integer> actualNumbers =
        LinearStream.generate(numberSequence::next, number -> number < 30).toList();

    List<Integer> expectedNumbers = List.of(3, 15, 28, 46);

    assertThat(actualNumbers).isEqualTo(expectedNumbers);
  }

  @Test
  void concatFirstEmpty() {

    List<Integer> actualConcatenation =
        LinearStream.concat(LinearStream.empty(), LinearStream.of(4, 5)).toList();

    List<Integer> expectedConcatenation = List.of(4, 5);

    assertThat(actualConcatenation).isEqualTo(expectedConcatenation);
  }

  @Test
  void concatSecondEmpty() {

    List<String> actualConcatenation =
        LinearStream.concat(
                LinearStream.of("Come", "and", "get", "your", "love"), LinearStream.empty())
            .toList();

    List<String> expectedConcatenation = List.of("Come", "and", "get", "your", "love");

    assertThat(actualConcatenation).isEqualTo(expectedConcatenation);
  }

  @Test
  void concatBothEmpty() {

    List<Object> actualConcatenation =
        LinearStream.concat(LinearStream.empty(), LinearStream.empty()).toList();

    assertThat(actualConcatenation).isEmpty();
  }
}
