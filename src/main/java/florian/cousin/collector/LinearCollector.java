package florian.cousin.collector;

import florian.cousin.LinearStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LinearCollector<T, A, R> {

  private final Supplier<A> supplier;
  private final BiConsumer<A, ? super T> accumulator;

  public abstract R collect(LinearStream<? extends T> linearStream);

  protected A collectWithoutFinisher(LinearStream<? extends T> linearStream) {
    A currentValue = supplier.get();
    linearStream.forEachRemaining(iteratedValue -> accumulator.accept(currentValue, iteratedValue));
    return currentValue;
  }

  public static <T, R> LinearCollector<T, R, R> of(
      Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return new SimpleCollector<>(supplier, accumulator);
  }

  public static <T, A, R> LinearCollector<T, A, R> of(
      Supplier<A> supplier, BiConsumer<A, ? super T> accumulator, Function<A, R> finisher) {
    return new CollectorFinisher<>(supplier, accumulator, finisher);
  }
}
