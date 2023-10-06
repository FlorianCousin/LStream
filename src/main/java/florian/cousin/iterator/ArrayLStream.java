package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

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
}
