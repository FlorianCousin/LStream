package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class takeWhileLStream<T> extends LStream<T> {

  private final Iterator<T> baseIterator;
  private final Predicate<? super T> predicate;

  private T next;
  private boolean nextIsConsumed = true;

  @Override
  public boolean hasNext() {

    dropUntilNext();

    return !nextIsConsumed && predicate.test(next);
  }

  @Override
  public T next() {

    if (hasNext()) {
      nextIsConsumed = true;
      return next;
    } else {
      throw new NoSuchElementException("the current element is not taken anymore");
    }
  }

  private void dropUntilNext() {

    if (nextIsConsumed && baseIterator.hasNext()) {
      nextIsConsumed = false;
      next = baseIterator.next();
    }
  }
}
