package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.*;
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
  public Object[] toArray() {

    if (nextIndex >= iterationObjects.length) {
      return new Object[0];
    }

    if (nextIndex == 0) {
      return iterationObjects;
    }

    return Arrays.copyOfRange(iterationObjects, nextIndex, iterationObjects.length);
  }

  @Override
  public ArrayLStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    Arrays.sort(iterationObjects, nextIndex, iterationObjects.length, comparator);
    return this;
  }

  @Override
  public List<T> toList() {

    if (nextIndex >= iterationObjects.length) {
      return Collections.emptyList();
    }

    List<T> allElements = Arrays.asList(iterationObjects);
    List<T> remainingElements = allElements.subList(nextIndex, iterationObjects.length);
    return Collections.unmodifiableList(remainingElements);
  }

  @Override
  public long count() {
    return Math.max(0, iterationObjects.length - nextIndex);
  }

  @Override
  public Optional<T> findLast() {

    if (hasNext()) {
      int lastElementIndex = iterationObjects.length - 1;
      T lastElement = iterationObjects[lastElementIndex];
      return Optional.ofNullable(lastElement);
    }

    return Optional.empty();
  }
}
