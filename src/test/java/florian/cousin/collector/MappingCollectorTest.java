package florian.cousin.collector;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class MappingCollectorTest {

  @Test
  void mappingEmpty() {

    String actualMapping =
        LinearStream.empty()
            .collect(LinearCollectors.mapping(s -> s + "a", LinearCollectors.joining()));

    String expectedMapping = "";

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }

  @Test
  void mappingNumbers() {

    List<Integer> actualMapping =
        LinearStream.of(0, 1, 2, 3, 4, 5)
            .collect(LinearCollectors.mapping(i -> i * i, LinearCollectors.toList()));

    List<Integer> expectedMapping = List.of(0, 1, 4, 9, 16, 25);

    assertThat(actualMapping).isEqualTo(expectedMapping);
  }

  @Test
  void flatMapping() {

    List<String> letters = List.of("a", "b", "c", "d");
    String actualFlatMapping =
        LinearStream.of(1, 0, 3)
            .collect(
                LinearCollectors.flatMapping(
                    nb -> LinearStream.from(nCopies(nb, letters.get(nb))),
                    LinearCollectors.joining(" ")));

    String expectedFlatMapping = "b d d d";

    assertThat(actualFlatMapping).isEqualTo(expectedFlatMapping);
  }
}
