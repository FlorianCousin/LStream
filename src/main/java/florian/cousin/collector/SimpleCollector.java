package florian.cousin.collector;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleCollector<T, A> extends LinearCollector<T, A, A> {

  public SimpleCollector(Supplier<A> supplier, BiConsumer<A, ? super T> accumulator) {
    super(supplier, accumulator, Function.identity());
  }
}
