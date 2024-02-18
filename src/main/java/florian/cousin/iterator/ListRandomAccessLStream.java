package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.*;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ListRandomAccessLStream<T> extends LStream<T> {

  private final List<T> iterationObjects;
  private int nextIndex = 0;

  @Override
  public boolean hasNext() {
    return iterationObjects.size() > nextIndex;
  }

  @Override
  public T next() {

    if (!hasNext()) {
      throw new NoSuchElementException("No more elements in the array.");
    }

    return iterationObjects.get(nextIndex++);
  }

  @Override
  public LStream<T> sorted(@Nullable Comparator<? super T> comparator) {

    if (!hasNext()) {
      return LStream.empty();
    }

    @SuppressWarnings("unchecked")
    T[] array = (T[]) iterationObjects.subList(nextIndex, iterationObjects.size()).toArray();

    // TODO Do not sort without consumption
    Arrays.sort(array, comparator);

    return new ListRandomAccessLStream<>(List.of(array));
  }

  @Override
  public LStream<T> limit(long maxSize) {

    if (nextIndex + maxSize >= iterationObjects.size()) {
      return this;
    }

    int lastIndexExclusive = Math.toIntExact(nextIndex + maxSize);
    List<T> newIterationObjects = iterationObjects.subList(nextIndex, lastIndexExclusive);

    return LStream.from(newIterationObjects);
  }

  @Override
  public ListRandomAccessLStream<T> skip(long nbToSkip) {
    requirePositive(nbToSkip);
    nextIndex = (int) Math.min(Integer.MAX_VALUE, nextIndex + nbToSkip);
    return this;
  }

  @Override
  public Object[] toArray() {

    if (!hasNext()) {
      return new Object[0];
    }

    return iterationObjects.subList(nextIndex, iterationObjects.size()).toArray();
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {

    if (!hasNext()) {
      return generator.apply(0);
    }

    return iterationObjects.subList(nextIndex, iterationObjects.size()).toArray(generator);
  }

  @Override
  public List<T> toList() {

    if (!hasNext()) {
      return Collections.emptyList();
    }

    List<T> remainingElements = iterationObjects.subList(nextIndex, iterationObjects.size());
    return Collections.unmodifiableList(remainingElements);
  }

  @Override
  public long count() {
    return Math.max(0, iterationObjects.size() - nextIndex);
  }

  @Override
  public Optional<T> findLast() {

    if (hasNext()) {
      int lastElementIndex = iterationObjects.size() - 1;
      T lastElement = iterationObjects.get(lastElementIndex);
      return Optional.ofNullable(lastElement);
    }

    return Optional.empty();
  }
}
