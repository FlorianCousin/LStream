package florian.cousin.collector;

import florian.cousin.LinearStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LinearCollector<T, A, R> {

  private final Supplier<A> supplier;
  private final BiConsumer<A, ? super T> accumulator;

  public abstract R collect(LinearStream<T> linearStream);

  protected A collectWithoutFinisher(LinearStream<T> linearStream) {
    A currentValue = supplier.get();
    linearStream.forEachRemaining(iteratedValue -> accumulator.accept(currentValue, iteratedValue));
    return currentValue;
  }
}
