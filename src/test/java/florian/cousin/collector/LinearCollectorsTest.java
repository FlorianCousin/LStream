package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class LinearCollectorsTest {

  @Test
  void toCollection() {

    TreeSet<String> actualCollection =
        LinearStream.of("a", "b", "d", "c").collect(LinearCollectors.toCollection(TreeSet::new));

    assertThat(actualCollection)
        .isExactlyInstanceOf(TreeSet.class)
        .containsExactly("a", "b", "c", "d");
  }
}
