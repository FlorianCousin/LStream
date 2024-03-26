package cousin.florian.collector;

import static org.assertj.core.api.Assertions.assertThat;

import cousin.florian.LStream;
import org.junit.jupiter.api.Test;

class JoiningCollectorTest {

  @Test
  void joiningSimple() {

    String actualJoin = LStream.of("j", "o", "i", "n").collect(LCollectors.joining());

    String expectedJoin = "join";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningEmpty() {

    String actualJoin = LStream.<String>empty().collect(LCollectors.joining());

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningOneValue() {

    String actualJoin = LStream.of("j").collect(LCollectors.joining());

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterSimple() {

    String actualJoin = LStream.of("j", "o", "i", "n").collect(LCollectors.joining("d"));

    String expectedJoin = "jdodidn";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterEmpty() {

    String actualJoin = LStream.<String>empty().collect(LCollectors.joining("d"));

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterOneValue() {

    String actualJoin = LStream.of("j").collect(LCollectors.joining("d"));

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixSimple() {

    String actualJoin =
        LStream.of("j", "o", "i", "n").collect(LCollectors.joining("d", "prefix", "suffix"));

    String expectedJoin = "prefixjdodidnsuffix";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixEmpty() {

    String actualJoin = LStream.<String>empty().collect(LCollectors.joining("d", "p", "s"));

    String expectedJoin = "ps";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixOneValue() {

    String actualJoin = LStream.of("j").collect(LCollectors.joining("d", "pre : \"", "\", end"));

    String expectedJoin = "pre : \"j\", end";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }
}
