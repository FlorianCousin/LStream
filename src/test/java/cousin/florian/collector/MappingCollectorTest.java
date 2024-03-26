package cousin.florian.collector;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class MappingCollectorTest {

  @Test
  void mappingEmpty() {

    String actualMapping =
        LStream.empty().collect(LCollectors.mapping(s -> s + "a", LCollectors.joining()));

    String expectedMapping = "";

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }

  @Test
  void mappingNumbers() {

    List<Integer> actualMapping =
        LStream.of(0, 1, 2, 3, 4, 5).collect(LCollectors.mapping(i -> i * i, LCollectors.toList()));

    List<Integer> expectedMapping = List.of(0, 1, 4, 9, 16, 25);

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }

  @Test
  void flatMapping() {

    List<String> letters = List.of("a", "b", "c", "d");
    String actualFlatMapping =
        LStream.of(1, 0, 3)
            .collect(
                LCollectors.flatMapping(
                    nb -> LStream.from(nCopies(nb, letters.get(nb))), LCollectors.joining(" ")));

    String expectedFlatMapping = "b d d d";

    assertThat(actualFlatMapping).isEqualTo(expectedFlatMapping);
  }
}
