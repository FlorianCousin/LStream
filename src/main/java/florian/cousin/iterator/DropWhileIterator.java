package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public class DropWhileIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;

  private @Nullable T firstTakenValue;
  private boolean firstValueIsConsumed;

  public DropWhileIterator(LinearStream<T> baseIterator, Predicate<? super T> predicate) {
    this.baseIterator = baseIterator;

    if (!baseIterator.hasNext()) {
      this.firstValueIsConsumed = true;
      this.firstTakenValue = null;
    }

    T currentNext = baseIterator.next();

    while (baseIterator.hasNext() && predicate.test(currentNext)) {
      currentNext = baseIterator.next();
    }

    this.firstTakenValue = currentNext;
    this.firstValueIsConsumed = predicate.test(currentNext);
  }

  @Override
  public boolean hasNext() {
    return !firstValueIsConsumed || baseIterator.hasNext();
  }

  @Override
  public T next() {

    if (!firstValueIsConsumed) {
      firstValueIsConsumed = true;
      return firstTakenValue;
    }

    return baseIterator.next();
  }
}
