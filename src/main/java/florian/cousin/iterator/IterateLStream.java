package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IterateLStream<T> extends LStream<T> {

  private final T initialValue;
  private final Predicate<? super T> hasNext;
  private final UnaryOperator<T> computeNextValue;

  private T previousValue;
  private boolean started = false;

  @Override
  public boolean hasNext() {
    return !started || hasNext.test(previousValue);
  }

  @Override
  public T next() {

    if (started) {
      return previousValue = computeNextValue.apply(previousValue);
    } else {
      started = true;
      return previousValue = initialValue;
    }
  }
}
