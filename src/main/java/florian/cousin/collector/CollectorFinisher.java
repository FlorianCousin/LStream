package florian.cousin.collector;

import florian.cousin.LinearStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CollectorFinisher<T, A, R> extends LinearCollector<T, A, R> {

  private final Function<A, R> finisher;

  public CollectorFinisher(
      Supplier<A> supplier, BiConsumer<A, ? super T> accumulator, Function<A, R> finisher) {
    super(supplier, accumulator);
    this.finisher = finisher;
  }

  @Override
  public R collect(LinearStream<? extends T> linearStream) {
    return finisher.apply(collectWithoutFinisher(linearStream));
  }

  @Override
  protected <U> LinearCollector<U, A, R> withAccumulator(
      BiConsumer<A, ? super U> overridingAccumulator) {
    return new CollectorFinisher<>(supplier(), overridingAccumulator, finisher);
  }
}
