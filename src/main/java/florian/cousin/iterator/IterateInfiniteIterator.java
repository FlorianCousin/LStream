package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IterateInfiniteIterator<T> implements LinearStream<T> {

  private final T initialValue;
  private final UnaryOperator<T> computeNextValue;

  private T previousValue;
  private boolean initialValueConsumed = false;

  @Override
  public boolean hasNext() {
    return true;
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
