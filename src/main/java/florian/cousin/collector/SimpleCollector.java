package florian.cousin.collector;

import florian.cousin.LinearStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SimpleCollector<T, A> extends LinearCollector<T, A, A> {

  public SimpleCollector(Supplier<A> supplier, BiConsumer<A, ? super T> accumulator) {
    super(supplier, accumulator);
  }

  @Override
  public A collect(LinearStream<? extends T> linearStream) {
    return collectWithoutFinisher(linearStream);
  }

  @Override
  protected <U> SimpleCollector<U, A> withAccumulator(
      BiConsumer<A, ? super U> overridingAccumulator) {
    return new SimpleCollector<>(supplier(), overridingAccumulator);
  }
}
