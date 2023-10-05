package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class FilteringCollectorTest {

  @Test
  void filtering() {

    List<Integer> actualFiltered =
        LStream.of(-1, 6, 0, -2, 7, 1, 6)
            .collect(LCollectors.filtering(nb -> nb > 0, LCollectors.toList()));

    List<Integer> expectedFiltered = List.of(6, 7, 1, 6);

    assertThat(actualFiltered).isEqualTo(expectedFiltered);
  }
}
