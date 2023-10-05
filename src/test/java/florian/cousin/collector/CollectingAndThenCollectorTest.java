package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CollectingAndThenCollectorTest {

  @Test
  void collectingAndThenAfterPreviousFinisher() {

    int actualCollectionAndThen =
        LStream.of("0", "1", "3")
            .collect(LCollectors.collectingAndThen(LCollectors.joining("2"), Integer::parseInt));

    int expectedCollectingAndThen = 2123;

    assertThat(actualCollectionAndThen).isEqualTo(expectedCollectingAndThen);
  }

  @Test
  void collectingAndThenAddFinisher() {

    List<String> actualCollectionAndThen =
        LStream.of("0", "1", "3")
            .collect(
                LCollectors.collectingAndThen(LCollectors.toList(), Collections::unmodifiableList));

    List<String> expectedCollectingAndThen = List.of("0", "1", "3");

    assertThat(actualCollectionAndThen).isUnmodifiable().isEqualTo(expectedCollectingAndThen);
  }
}
