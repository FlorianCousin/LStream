package florian.cousin.collector;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import florian.cousin.LinearStream;
import java.util.*;
import java.util.function.Function;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class GroupingByCollectorTest {

  @Test
  void groupingByEmpty() {

    Map<Integer, List<Integer>> actualGroups =
        LinearStream.<Integer>empty().collect(LinearCollectors.groupingBy(Function.identity()));

    assertThat(actualGroups).isEmpty();
  }

  @Test
  void groupingByOneElement() {

    Map<Integer, List<String>> actualGroups =
        LinearStream.of("Long.MAX_VALUE").collect(LinearCollectors.groupingBy(String::length));

    Map<Integer, List<String>> expectedGroups = Map.of(14, singletonList("Long.MAX_VALUE"));

    assertThat(actualGroups).isEqualTo(expectedGroups);
  }

  @Test
  void groupingByElements() {

    Map<Integer, List<String>> actualGroups =
        LinearStream.of("word", "Long", "2L").collect(LinearCollectors.groupingBy(String::length));

    Map<Integer, List<String>> expectedGroups =
        Map.ofEntries(Map.entry(4, List.of("word", "Long")), Map.entry(2, singletonList("2L")));

    assertThat(actualGroups).isEqualTo(expectedGroups);
  }

  @Test
  void groupingByDownstreamEmpty() {

    Map<Integer, Set<Integer>> actualGroups =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.groupingBy(
                    Function.identity(), LinearCollectors.toUnmodifiableSet()));

    assertThat(actualGroups).isEmpty();
  }

  @Test
  void groupingByDownstreamOneElement() {

    Map<Integer, Long> actualGroups =
        LinearStream.of("Long.MAX_VALUE")
            .collect(LinearCollectors.groupingBy(String::length, LinearCollectors.counting()));

    Map<Integer, Long> expectedGroups = Map.of(14, 1L);

    assertThat(actualGroups).isEqualTo(expectedGroups);
  }

  @Test
  void groupingByDownstreamElements() {

    Map<Integer, String> actualGroups =
        LinearStream.of("word", "Long", "2L")
            .collect(LinearCollectors.groupingBy(String::length, LinearCollectors.joining("-")));

    Map<Integer, String> expectedGroups =
        Map.ofEntries(Map.entry(4, "word-Long"), Map.entry(2, "2L"));

    assertThat(actualGroups).isEqualTo(expectedGroups);
  }

  @Test
  void groupingByDownstreamFactoryElements() {

    Map<Integer, String> actualGroups =
        LinearStream.of("word", "Long", "2L")
            .collect(
                LinearCollectors.groupingBy(
                    String::length, TreeMap::new, LinearCollectors.joining("-")));

    Map<Integer, String> expectedGroups =
        Map.ofEntries(Map.entry(4, "word-Long"), Map.entry(2, "2L"));

    assertThat(actualGroups).isEqualTo(expectedGroups);
  }

  @Test
  void groupingByDownstreamUnmodifiableFactoryElements() {

    ThrowableAssert.ThrowingCallable buildActualMap =
        () ->
            LinearStream.of("word", "Long", "2L")
                .collect(
                    LinearCollectors.groupingBy(
                        String::length, Collections::emptyMap, LinearCollectors.joining("-")));

    assertThatThrownBy(buildActualMap)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage(null);
  }
}
