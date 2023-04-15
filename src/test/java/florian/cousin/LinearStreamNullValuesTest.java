package florian.cousin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LinearStreamNullValuesTest {

  @Test
  void filter() {

    List<Integer> actualNullValues = LinearStream.of(1, null, 6).filter(Objects::isNull).toList();

    Assertions.assertThat(actualNullValues).containsExactlyElementsOf(Collections.singleton(null));
  }
}
