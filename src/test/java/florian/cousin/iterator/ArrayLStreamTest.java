package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ArrayLStreamTest {

  @Test
  void toList() {

    LStream<Integer> arrayLStream = LStream.of(1, 2, 3, 4);

    arrayLStream.next();

    List<Integer> actualElementsLeft = arrayLStream.toList();

    Assertions.assertThat(actualElementsLeft).isUnmodifiable().containsExactly(2, 3, 4);
  }

  @Test
  void toListWithNull() {

    List<Integer> actualValuesWithNull = LStream.of(1, null).toList();

    Assertions.assertThat(actualValuesWithNull).isUnmodifiable().containsExactly(1, null);
  }

  @Test
  void skip() {

    LStream<Integer> arrayLStream = LStream.of(1, 2, 3, 4);

    arrayLStream.next();

    List<Integer> actualNotSkipped = arrayLStream.skip(2).toList();

    Assertions.assertThat(actualNotSkipped).containsExactly(4);
  }

  @Test
  void sorted() {

    LStream<Integer> arrayLStream = LStream.of(5, 7, 6, 3, 8);

    arrayLStream.next();

    List<Integer> actualSortedValues = arrayLStream.sorted().toList();

    Assertions.assertThat(actualSortedValues).containsExactly(3, 6, 7, 8);
  }
}
