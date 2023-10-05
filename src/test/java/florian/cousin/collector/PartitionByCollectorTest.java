package florian.cousin.collector;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartitionByCollectorTest {

  @Test
  void partitioningByEmpty() {

    Map<Boolean, List<Integer>> actualPartition =
        LStream.<Integer>empty().collect(LCollectors.partitioningBy(i -> i < 0));

    assertThat(actualPartition).isEmpty();
  }

  @Test
  void partitioningByOneElement() {

    Map<Boolean, List<String>> actualPartition =
        LStream.of("Long.MAX_VALUE").collect(LCollectors.partitioningBy(s -> s.startsWith("L")));

    Map<Boolean, List<String>> expectedPartition = Map.of(true, singletonList("Long.MAX_VALUE"));

    assertThat(actualPartition).isEqualTo(expectedPartition);
  }

  @Test
  void partitioningByElements() {

    Map<Boolean, List<String>> actualPartition =
        LStream.of("word", "Long", "2L")
            .collect(LCollectors.partitioningBy(s -> s.startsWith("L") || s.endsWith("L")));

    Map<Boolean, List<String>> expectedPartition =
        Map.ofEntries(
            Map.entry(true, List.of("Long", "2L")), Map.entry(false, singletonList("word")));

    assertThat(actualPartition).isEqualTo(expectedPartition);
  }

  @Test
  void partitioningByDownstreamEmpty() {

    Map<Boolean, Set<Integer>> actualPartition =
        LStream.<Integer>empty()
            .collect(LCollectors.partitioningBy(i -> i > 0, LCollectors.toUnmodifiableSet()));

    assertThat(actualPartition).isEmpty();
  }

  @Test
  void partitioningByDownstreamOneElement() {

    Map<Boolean, Long> actualPartition =
        LStream.of("Long.MAX_VALUE")
            .collect(LCollectors.partitioningBy(s -> s.startsWith("o"), LCollectors.counting()));

    Map<Boolean, Long> expectedPartition = Map.of(false, 1L);

    assertThat(actualPartition).isEqualTo(expectedPartition);
  }

  @Test
  void partitioningByDownstreamElements() {

    Map<Boolean, String> actualPartition =
        LStream.of("word", "Long", "2L")
            .collect(LCollectors.partitioningBy(s -> 'o' == s.charAt(1), LCollectors.joining("-")));

    Map<Boolean, String> expectedPartition =
        Map.ofEntries(Map.entry(true, "word-Long"), Map.entry(false, "2L"));

    assertThat(actualPartition).isEqualTo(expectedPartition);
  }
}
