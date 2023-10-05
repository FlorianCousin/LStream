package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilterLStream<T> extends LStream<T> {

  private final Iterator<T> iterator;
  private final Predicate<? super T> predicate;

  private T next;
  private boolean nextIsConsumed = true;

  @Override
  public boolean hasNext() {

    dropUntilValue();

    return !nextIsConsumed;
  }

  @Override
  public T next() {

    if (hasNext()) {
      nextIsConsumed = true;
      return next;
    } else {
      throw new NoSuchElementException("No more elements in filter iterator.");
    }
  }

  private void dropUntilValue() {
    while (nextIsConsumed && iterator.hasNext()) {
      T currentNext = iterator.next();
      if (predicate.test(currentNext)) {
        next = currentNext;
        nextIsConsumed = false;
      }
    }
  }
}
