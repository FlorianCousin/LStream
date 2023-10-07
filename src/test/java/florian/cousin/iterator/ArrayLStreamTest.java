package florian.cousin.iterator;

import static org.assertj.core.api.Assertions.assertThat;

import florian.cousin.LStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArrayLStreamTest {

  @Test
  void toList() {

    LStream<Integer> arrayLStream = LStream.of(1, 2, 3, 4);

    arrayLStream.next();

    List<Integer> actualElementsLeft = arrayLStream.toList();

    assertThat(actualElementsLeft).isUnmodifiable().containsExactly(2, 3, 4);
  }

  @Test
  void toListWithNull() {

    List<Integer> actualValuesWithNull = LStream.of(1, null).toList();

    assertThat(actualValuesWithNull).isUnmodifiable().containsExactly(1, null);
  }

  @Test
  void skip() {

    LStream<Integer> arrayLStream = LStream.of(1, 2, 3, 4);

    arrayLStream.next();

    LStream<Integer> arrayLStreamNotSkipped = arrayLStream.skip(2);

    // ArrayLStream#skip is called
    assertThat(arrayLStreamNotSkipped).isEqualTo(arrayLStream);

    List<Integer> actualNotSkipped = arrayLStreamNotSkipped.toList();

    // Only remaining values are skipped
    assertThat(actualNotSkipped).containsExactly(4);
  }

  @Test
  void sorted() {

    LStream<Integer> arrayLStream = LStream.of(5, 7, 6, 3, 8);

    arrayLStream.next();

    LStream<Integer> arrayLStreamSorted = arrayLStream.sorted();

    // ArrayLStream#sort is called
    assertThat(arrayLStreamSorted).isEqualTo(arrayLStream);

    List<Integer> actualSortedValues = arrayLStreamSorted.toList();

    // Only remaining values are sorted
    assertThat(actualSortedValues).containsExactly(3, 6, 7, 8);
  }

  @Test
  void toArrayObject() {

    LStream<Integer> lStream = LStream.of(1, 2, 3, 4);

    lStream.next();

    assertThat(lStream.toArray()).isEqualTo(new Object[] {2, 3, 4});
  }

  @Test
  void countSkipOne() {

    LStream<Integer> lStream = LStream.of(1, 2, 3, 4).skip(1);

    assertThat(lStream.count()).isEqualTo(3);
  }

  @Test
  void countSkipALot() {

    LStream<Integer> lStream = LStream.of(1, 2, 3, 4).skip(10);

    assertThat(lStream.count()).isZero();
  }
}
