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
}
