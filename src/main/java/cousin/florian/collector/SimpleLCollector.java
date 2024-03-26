package cousin.florian.collector;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleLCollector<T, A> extends LCollector<T, A, A> {

  SimpleLCollector(Supplier<A> supplier, BiConsumer<A, ? super T> accumulator) {
    super(supplier, accumulator, Function.identity());
  }
}
