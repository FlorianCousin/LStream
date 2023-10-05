package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class TeeingCollectorTest {

  @Test
  void teeingEmpty() {

    List<Integer> actualCollection =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.teeing(
                    LinearCollectors.toList(),
                    LinearCollectors.toUnmodifiableSet(),
                    (list, set) -> {
                      list.addAll(set);
                      return list;
                    }));

    assertThat(actualCollection).isEmpty();
  }

  @Test
  void teeingElements() {

    List<Integer> actualCollection =
        LinearStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(
                LinearCollectors.teeing(
                    LinearCollectors.mapping(String::length, LinearCollectors.toList()),
                    LinearCollectors.counting(),
                    (list, nbElements) -> {
                      list.add(Math.toIntExact(nbElements));
                      return list;
                    }));

    List<Integer> expectedCollection = List.of(7, 10, 7, 7, 6, 5);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }
}
