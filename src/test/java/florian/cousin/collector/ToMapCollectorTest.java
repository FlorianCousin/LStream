package florian.cousin.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import florian.cousin.LinearStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

class ToMapCollectorTest {

  @Test
  void toMapEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(LinearCollectors.toMap(Function.identity(), Function.identity()));

    assertThat(actualMap).isEmpty();
  }

  @Test
  void toMapOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(LinearCollectors.toMap(String::length, Function.identity()));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapElements() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE", "Long", "2L")
            .collect(LinearCollectors.toMap(String::length, s -> s.substring(1)));

    Map<Integer, String> expectedMap =
        Map.ofEntries(Map.entry(14, "ong.MAX_VALUE"), Map.entry(4, "ong"), Map.entry(2, "L"));

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapDuplicateKey() {

    ThrowableAssert.ThrowingCallable buildActualMap =
        () ->
            LinearStream.of("hi", "word", "mine")
                .collect(LinearCollectors.toMap(String::length, Function.identity()));

    assertThatThrownBy(buildActualMap)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Duplicate key 4 (attempted to add value mine but word already existed)");
  }

  @Test
  void toMapMergeEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.toMap(Function.identity(), Function.identity(), Integer::sum));

    assertThat(actualMap).isEmpty();
  }

  @Test
  void toMapMergeOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(
                LinearCollectors.toMap(
                    String::length, Function.identity(), (s1, s2) -> String.join("-", s1, s2)));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapMergeElements() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE", "Long", "2L")
            .collect(
                LinearCollectors.toMap(
                    String::length, s -> s.substring(1), (s1, s2) -> String.join("-", s1, s2)));

    Map<Integer, String> expectedMap =
        Map.ofEntries(Map.entry(14, "ong.MAX_VALUE"), Map.entry(4, "ong"), Map.entry(2, "L"));

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapMergeDuplicateKey() {

    Map<Integer, String> actualMap =
        LinearStream.of("hi", "word", "mine")
            .collect(
                LinearCollectors.toMap(
                    String::length, Function.identity(), (s1, s2) -> String.join("-", s1, s2)));

    Map<Integer, String> expectedMap = Map.ofEntries(Map.entry(4, "word-mine"), Map.entry(2, "hi"));

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapFactoryEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.toMap(
                    Function.identity(), Function.identity(), Collections::emptyMap));

    assertThat(actualMap).isEmpty();
  }

  @Test
  void toMapFactoryOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(
                LinearCollectors.toMap(
                    String::length, Function.identity(), ConcurrentSkipListMap::new));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapFactoryElements() {

    ThrowableAssert.ThrowingCallable buildActualMap =
        () ->
            LinearStream.of("Long.MAX_VALUE", "Long", "2L")
                .collect(
                    LinearCollectors.toMap(
                        String::length, Function.identity(), Collections::emptyMap));

    assertThatThrownBy(buildActualMap)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage(null);
  }

  @Test
  void toMapMergeFactoryEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.toMap(
                    Function.identity(), Function.identity(), Integer::sum, Collections::emptyMap));

    assertThat(actualMap).isEmpty();
  }

  @Test
  void toMapMergeFactoryOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(
                LinearCollectors.toMap(
                    String::length,
                    Function.identity(),
                    (s1, s2) -> String.join("-", s1, s2),
                    ConcurrentSkipListMap::new));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isEqualTo(expectedMap);
  }

  @Test
  void toMapMergeFactoryElements() {

    ThrowableAssert.ThrowingCallable buildActualMap =
        () ->
            LinearStream.of("Long.MAX_VALUE", "Long", "2L")
                .collect(
                    LinearCollectors.toMap(
                        String::length,
                        Function.identity(),
                        (s1, s2) -> String.join("-", s1, s2),
                        Collections::emptyMap));

    assertThatThrownBy(buildActualMap)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage(null);
  }

  @Test
  void toUnmodifiableMapEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(LinearCollectors.toUnmodifiableMap(Function.identity(), Function.identity()));

    assertThat(actualMap).isUnmodifiable().isEmpty();
  }

  @Test
  void toUnmodifiableMapOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(LinearCollectors.toUnmodifiableMap(String::length, Function.identity()));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isUnmodifiable().isEqualTo(expectedMap);
  }

  @Test
  void toUnmodifiableMapElements() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE", "Long", "2L")
            .collect(LinearCollectors.toUnmodifiableMap(String::length, s -> s.substring(1)));

    Map<Integer, String> expectedMap =
        Map.ofEntries(Map.entry(14, "ong.MAX_VALUE"), Map.entry(4, "ong"), Map.entry(2, "L"));

    assertThat(actualMap).isUnmodifiable().isEqualTo(expectedMap);
  }

  @Test
  void toUnmodifiableMapMergeEmpty() {

    Map<Integer, Integer> actualMap =
        LinearStream.<Integer>empty()
            .collect(
                LinearCollectors.toUnmodifiableMap(
                    Function.identity(), Function.identity(), Integer::sum));

    assertThat(actualMap).isUnmodifiable().isEmpty();
  }

  @Test
  void toUnmodifiableMapMergeOneElement() {

    Map<Integer, String> actualMap =
        LinearStream.of("Long.MAX_VALUE")
            .collect(
                LinearCollectors.toUnmodifiableMap(
                    String::length, Function.identity(), (s1, s2) -> String.join("-", s1, s2)));

    Map<Integer, String> expectedMap = Map.of(14, "Long.MAX_VALUE");

    assertThat(actualMap).isUnmodifiable().isEqualTo(expectedMap);
  }

  @Test
  void toUnmodifiableMapMergeElements() {

    Map<Integer, String> actualMap =
        LinearStream.of("hi", "word", "mine")
            .collect(
                LinearCollectors.toUnmodifiableMap(
                    String::length, s -> s.substring(1), (s1, s2) -> String.join("-", s1, s2)));

    Map<Integer, String> expectedMap = Map.ofEntries(Map.entry(4, "ord-ine"), Map.entry(2, "i"));

    assertThat(actualMap).isUnmodifiable().isEqualTo(expectedMap);
  }
}
