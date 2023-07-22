package florian.cousin.collector;

import florian.cousin.LinearStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class LinearCollector<T, A, R> {

  private final Supplier<A> supplier;
  private final BiConsumer<A, ? super T> accumulator;
  private final Function<A, R> finisher;

  public R collect(LinearStream<? extends T> linearStream) {
    A currentValue = supplier.get();
    linearStream.forEachRemaining(iteratedValue -> accumulator.accept(currentValue, iteratedValue));
    return finisher.apply(currentValue);
  }

  public static <T, R> LinearCollector<T, R, R> of(
      Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return new SimpleCollector<>(supplier, accumulator);
  }

  public static <T, A, R> LinearCollector<T, A, R> of(
      Supplier<A> supplier, BiConsumer<A, ? super T> accumulator, Function<A, R> finisher) {
    return new LinearCollector<>(supplier, accumulator, finisher);
  }

  protected <U> LinearCollector<U, A, R> withAccumulator(
      BiConsumer<A, ? super U> overridingAccumulator) {
    return new LinearCollector<>(supplier, overridingAccumulator, finisher);
  }
}
