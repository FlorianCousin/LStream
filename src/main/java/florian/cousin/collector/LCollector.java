package florian.cousin.collector;

import static lombok.AccessLevel.PACKAGE;

import florian.cousin.LStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO Add a LCollectorApi ?
@Getter(PACKAGE)
@RequiredArgsConstructor(access = PACKAGE)
public class LCollector<T, A, R> {

  private final Supplier<A> supplier;
  private final BiConsumer<A, ? super T> accumulator;
  private final Function<A, R> finisher;

  public R collect(LStream<? extends T> lStream) {
    A currentValue = supplier.get();
    lStream.forEachRemaining(iteratedValue -> accumulator.accept(currentValue, iteratedValue));
    return finisher.apply(currentValue);
  }

  public static <T, R> SimpleLCollector<T, R> of(
      Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return new SimpleLCollector<>(supplier, accumulator);
  }

  public static <T, A, R> LCollector<T, A, R> of(
      Supplier<A> supplier, BiConsumer<A, ? super T> accumulator, Function<A, R> finisher) {
    return new LCollector<>(supplier, accumulator, finisher);
  }

  protected <U> LCollector<U, A, R> withAccumulator(
      BiConsumer<A, ? super U> overridingAccumulator) {
    return new LCollector<>(supplier, overridingAccumulator, finisher);
  }

  protected <S> LCollector<T, A, S> collectingAndThen(Function<R, S> afterFinisher) {
    return new LCollector<>(supplier, accumulator, finisher.andThen(afterFinisher));
  }
}
