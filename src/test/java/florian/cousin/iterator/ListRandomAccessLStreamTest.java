package florian.cousin.iterator;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListRandomAccessLStreamTest {

  @Test
  void toList() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4));

    lStream.next();

    List<Integer> actualElementsLeft = lStream.toList();

    assertThat(actualElementsLeft).isUnmodifiable().containsExactly(2, 3, 4);
  }

  @Test
  void toListWithNull() {

    List<Integer> actualValuesWithNull = LStream.from(Arrays.asList(1, null)).toList();

    assertThat(actualValuesWithNull).isUnmodifiable().containsExactly(1, null);
  }

  @Test
  void skip() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4));

    lStream.next();

    LStream<Integer> arrayLStreamAfterSkip = lStream.skip(2);

    // ArrayLStream#skip is called
    assertThat(arrayLStreamAfterSkip).isEqualTo(lStream);

    List<Integer> actualNotSkipped = arrayLStreamAfterSkip.toList();

    // Only remaining values are skipped
    assertThat(actualNotSkipped).containsExactly(4);
  }

  @Test
  void toArrayObject() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4));

    lStream.next();

    assertThat(lStream.toArray()).isEqualTo(new Object[] {2, 3, 4});
  }

  @Test
  void toArrayGenerator() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4));

    lStream.next();

    assertThat(lStream.toArray(Integer[]::new)).isEqualTo(new Integer[] {2, 3, 4});
  }

  @Test
  void countSkipOne() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4)).skip(1);

    assertThat(lStream.count()).isEqualTo(3);
  }

  @Test
  void countSkipALot() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4)).skip(10);

    assertThat(lStream.count()).isZero();
  }

  @Test
  void findLastSkipOne() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4)).skip(1);

    assertThat(lStream.findLast()).hasValue(4);
  }

  @Test
  void findLastSkipALot() {

    LStream<Integer> lStream = LStream.from(List.of(1, 2, 3, 4)).skip(10);

    assertThat(lStream.findLast()).isEmpty();
  }
}
