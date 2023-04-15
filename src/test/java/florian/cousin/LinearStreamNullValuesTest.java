package florian.cousin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    List<Integer> expectedValues = buildArrayList(3, null);

    assertThat(actualValues).isEqualTo(expectedValues);
  }

  @SafeVarargs
  private <T> List<T> buildArrayList(T... values) {
    ArrayList<T> list = new ArrayList<>();
    Collections.addAll(list, values);
    return list;
  }
}
