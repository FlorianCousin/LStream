package florian.cousin;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.assertj.core.api.Assertions;
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

    Assertions.assertThat(actualFiltered)
        .withFailMessage("filter function has not been called")
        .isEqualTo(expectedFiltered);
  }

  @Test
  void map() {

    List<Integer> actualLengths =
        LinearStream.of("bonjour", "hi", "hello", "hola").map(String::length).toList();

    List<Integer> expectedLengths = List.of(7, 2, 5, 4);

    Assertions.assertThat(actualLengths)
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

    Assertions.assertThat(actualFlatMap).isEqualTo(expectedFlatMap);
  }

  @Test
  void maxWithReduce() {

    int actualMaximum = LinearStream.of(1, 5, 9, 3, -14, 6, 753, 0).reduce(0, Math::max);

    int expectedMaximum = 753;

    Assertions.assertThat(actualMaximum).isEqualTo(expectedMaximum);
  }
}
