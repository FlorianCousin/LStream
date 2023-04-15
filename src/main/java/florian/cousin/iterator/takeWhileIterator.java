package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class takeWhileIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;
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
