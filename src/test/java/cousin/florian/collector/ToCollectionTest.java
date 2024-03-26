package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class ToCollectionTest {

  @Test
  void toCollection() {

    TreeSet<String> actualCollection =
        LStream.of("a", "b", "d", "c").collect(LCollectors.toCollection(TreeSet::new));

    assertThat(actualCollection)
        .isExactlyInstanceOf(TreeSet.class)
        .containsExactly("a", "b", "c", "d");
  }
}
