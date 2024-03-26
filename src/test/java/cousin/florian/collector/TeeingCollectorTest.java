package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class TeeingCollectorTest {

  @Test
  void teeingEmpty() {

    List<Integer> actualCollection =
        LStream.<Integer>empty()
            .collect(
                LCollectors.teeing(
                    LCollectors.toList(),
                    LCollectors.toUnmodifiableSet(),
                    (list, set) -> {
                      list.addAll(set);
                      return list;
                    }));

    assertThat(actualCollection).isEmpty();
  }

  @Test
  void teeingElements() {

    List<Integer> actualCollection =
        LStream.of("Florian", "Clémentine", "Chantal", "Laurent", "Thomas")
            .collect(
                LCollectors.teeing(
                    LCollectors.mapping(String::length, LCollectors.toList()),
                    LCollectors.counting(),
                    (list, nbElements) -> {
                      list.add(Math.toIntExact(nbElements));
                      return list;
                    }));

    List<Integer> expectedCollection = List.of(7, 10, 7, 7, 6, 5);

    assertThat(actualCollection).isEqualTo(expectedCollection);
  }
}
