package florian.cousin.iterator;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class EmptyLStreamTest {

  @Test
  void filter() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.filter(Predicate.not("3"::equals)));
  }

  @Test
  void distinct() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.distinct());
  }

  @Test
  void sorted() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.sorted());
  }

  @Test
  void sortedWithComparator() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.sorted(Comparator.comparing(o -> "3")));
  }

  @Test
  void peek() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.peek(Object::notify));
  }

  @Test
  void limit() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.limit(6));
  }

  @Test
  void skip() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.skip(0));
  }

  @Test
  void takeWhile() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.takeWhile(Predicate.not("3"::equals)));
  }

  @Test
  void takeWhilePrevious() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.takeWhilePrevious(Predicate.not("3"::equals)));
  }

  @Test
  void dropWhile() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream).isEqualTo(emptyLStream.dropWhile(Predicate.not("3"::equals)));
  }

  @Test
  void toArray() {

    EmptyLStream<Integer> emptyLStream = LStream.empty();

    assertThat(emptyLStream.toArray()).isEmpty();
  }

  @Test
  void toArrayWithSupplier() {

    EmptyLStream<Integer> emptyLStream = LStream.empty();

    assertThat(emptyLStream.toArray(Integer[]::new)).isEmpty();
  }

  @Test
  void reduceAccumulatorOnly() {

    EmptyLStream<String> emptyLStream = LStream.empty();

    assertThat(emptyLStream.reduce((s1, s2) -> s1 + s2)).isEmpty();
  }

  @Test
  void reduceIdentityAccumulator() {

    EmptyLStream<Integer> emptyLStream = LStream.empty();

    assertThat(emptyLStream.reduce(-3, Integer::sum)).isEqualTo(-3);
  }

  @Test
  void reduceIdentityAggregate() {

    EmptyLStream<String> emptyLStream = LStream.empty();

    assertThat(emptyLStream.reduce(3, (i, s) -> i + s.length())).isEqualTo(3);
  }

  @Test
  void collect() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.collect(() -> new ConcurrentHashMap<>(), (map, o) -> map.put(o, o)))
        .isInstanceOf(ConcurrentHashMap.class)
        .isEmpty();
  }

  @Test
  void toList() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.toList()).isUnmodifiable().isEmpty();
  }

  @Test
  void min() {

    EmptyLStream<Integer> emptyLStream = LStream.empty();

    assertThat(emptyLStream.min(Comparator.naturalOrder())).isEmpty();
  }

  @Test
  void max() {

    EmptyLStream<String> emptyLStream = LStream.empty();

    assertThat(emptyLStream.max(Comparator.naturalOrder())).isEmpty();
  }

  @Test
  void count() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.count()).isZero();
  }

  @Test
  void anyMatch() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.anyMatch(o -> true)).isFalse();
  }

  @Test
  void allMatch() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.allMatch(o -> false)).isTrue();
  }

  @Test
  void noneMatch() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.noneMatch(o -> true)).isTrue();
  }

  @Test
  void findFirst() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.findFirst()).isEmpty();
  }

  @Test
  void findOne() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.findOne()).isEmpty();
  }

  @Test
  void findLast() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.findLast()).isEmpty();
  }

  @Test
  void iterator() {

    EmptyLStream<Object> emptyLStream = LStream.empty();

    assertThat(emptyLStream.iterator()).isExhausted();
  }
}
