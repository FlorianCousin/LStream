package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LinearStream;
import org.junit.jupiter.api.Test;

class JoiningCollectorTest {

  @Test
  void joiningSimple() {

    String actualJoin = LinearStream.of("j", "o", "i", "n").collect(LinearCollectors.joining());

    String expectedJoin = "join";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningEmpty() {

    String actualJoin = LinearStream.<String>empty().collect(LinearCollectors.joining());

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningOneValue() {

    String actualJoin = LinearStream.of("j").collect(LinearCollectors.joining());

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterSimple() {

    String actualJoin = LinearStream.of("j", "o", "i", "n").collect(LinearCollectors.joining("d"));

    String expectedJoin = "jdodidn";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterEmpty() {

    String actualJoin = LinearStream.<String>empty().collect(LinearCollectors.joining("d"));

    String expectedJoin = "";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterOneValue() {

    String actualJoin = LinearStream.of("j").collect(LinearCollectors.joining("d"));

    String expectedJoin = "j";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixSimple() {

    String actualJoin =
        LinearStream.of("j", "o", "i", "n")
            .collect(LinearCollectors.joining("d", "prefix", "suffix"));

    String expectedJoin = "prefixjdodidnsuffix";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixEmpty() {

    String actualJoin =
        LinearStream.<String>empty().collect(LinearCollectors.joining("d", "p", "s"));

    String expectedJoin = "ps";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }

  @Test
  void joiningDelimiterPrefixSuffixOneValue() {

    String actualJoin =
        LinearStream.of("j").collect(LinearCollectors.joining("d", "pre : \"", "\", end"));

    String expectedJoin = "pre : \"j\", end";

    assertThat(actualJoin).isEqualTo(expectedJoin);
  }
}
