package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/** Does not support null values */
@RequiredArgsConstructor
public class takeWhileIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;
  private final Predicate<? super T> predicate;

  private @Nullable T next;

  @Override
  public boolean hasNext() {

    next = computeNext();

    return (next != null || baseIterator.hasNext()) && predicate.test(next);
  }

  @Override
  public T next() {

    if (hasNext()) {
      T currentNext = next;
      next = null;
      return currentNext;
    } else {
      throw new NoSuchElementException("the current element is not taken anymore");
    }
  }

  private @Nullable T computeNext() {

    if (next == null && baseIterator.hasNext()) {
      return Objects.requireNonNull(
          baseIterator.next(), "null values are not allowed in TakeWhileIterator");
    } else {
      return next;
    }
  }
}
