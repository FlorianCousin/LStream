package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IterateIterator<T> implements LinearStream<T> {

  private final T initialValue;
  private final Predicate<? super T> hasNext;
  private final UnaryOperator<T> computeNextValue;

  private T previousValue;
  private boolean initialValueConsumed = false;

  @Override
  public boolean hasNext() {
    return !initialValueConsumed || hasNext.test(previousValue);
  }

  @Override
  public T next() {

    if (initialValueConsumed) {
      return previousValue = computeNextValue.apply(previousValue);
    } else {
      initialValueConsumed = true;
      return previousValue = initialValue;
    }
  }
}
