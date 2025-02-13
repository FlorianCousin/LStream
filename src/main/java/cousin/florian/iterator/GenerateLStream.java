package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateLStream<T> extends LStream<T> {

  private final Supplier<? extends T> nextValueGenerator;
  private final Predicate<? super T> hasNext;

  private T previousValue;
  private boolean started;

  @Override
  public boolean hasNext() {
    return !started || hasNext.test(previousValue);
  }

  @Override
  public T next() {

    if (!started) {
      started = true;
    }

    return previousValue = nextValueGenerator.get();
  }
}
