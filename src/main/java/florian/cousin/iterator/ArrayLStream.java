package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ArrayLStream<T> extends LStream<T> {

  private final T[] iterationObjects;
  private int nextIndex = 0;

  @Override
  public boolean hasNext() {
    return iterationObjects.length > nextIndex;
  }

  @Override
  public T next() {

    if (!hasNext()) {
      throw new NoSuchElementException("No more elements in the array.");
    }

    return iterationObjects[nextIndex++];
  }

  @Override
  public ArrayLStream<T> skip(long nbToSkip) {
    requirePositive(nbToSkip);
    nextIndex = (int) Math.min(Integer.MAX_VALUE, nextIndex + nbToSkip);
    return this;
  }

  @Override
  public ArrayLStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    Arrays.sort(iterationObjects, nextIndex, iterationObjects.length, comparator);
    return this;
  }
}
