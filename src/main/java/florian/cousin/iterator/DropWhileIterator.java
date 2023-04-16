package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropWhileIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;
  private final Predicate<? super T> predicate;

  private T firstNotDroppedValue;
  private boolean firstIsConsumed = true;
  private boolean started = false;

  @Override
  public boolean hasNext() {

    dropUntilNext();

    return !firstIsConsumed || baseIterator.hasNext();
  }

  @Override
  public T next() {

    dropUntilNext();

    started = true;

    if (firstIsConsumed) {
      return baseIterator.next();
    }

    firstIsConsumed = true;
    return firstNotDroppedValue;
  }

  private void dropUntilNext() {

    while (!started && firstIsConsumed && baseIterator.hasNext()) {
      T currentNext = baseIterator.next();
      if (predicate.negate().test(currentNext)) {
        firstIsConsumed = false;
        firstNotDroppedValue = currentNext;
      }
    }
  }
}
