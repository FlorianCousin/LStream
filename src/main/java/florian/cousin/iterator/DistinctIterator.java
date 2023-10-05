package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DistinctIterator<T> extends LStream<T> {

  private final Iterator<T> iterator;
  private final Set<T> iteratedElements = new HashSet<>();

  private T next;
  private boolean nextIsConsumed = true;

  @Override
  public boolean hasNext() {

    dropUntilNext();

    return !nextIsConsumed;
  }

  @Override
  public T next() {

    if (hasNext()) {
      nextIsConsumed = true;
      return next;
    } else {
      throw new NoSuchElementException("No more elements in distinct iterator.");
    }
  }

  private void dropUntilNext() {
    while (nextIsConsumed && iterator.hasNext()) {
      T currentNext = iterator.next();
      if (!iteratedElements.contains(currentNext)) {
        iteratedElements.add(currentNext);
        nextIsConsumed = false;
        next = currentNext;
      }
    }
  }
}
