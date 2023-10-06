package florian.cousin;

import florian.cousin.collector.LCollector;
import florian.cousin.exception.SeveralElementsException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public interface LStreamApi<T> {

  /**
   * Returns a lstream consisting of the elements of this lstream that match the given predicate.
   *
   * <p>This is an intermediate operation.
   *
   * @param predicate a predicate to apply to each element to determine if it should be included
   * @return the new lstream
   */
  LStreamApi<T> filter(Predicate<? super T> predicate);

  /**
   * Returns a lstream consisting of the results of applying the given function to the elements of
   * this lstream.
   *
   * <p>This is an intermediate operation.
   *
   * @param <R> The element type of the new lstream
   * @param mapper a function to apply to each element
   * @return the new lstream
   */
  <R> LStreamApi<R> map(Function<? super T, ? extends R> mapper);

  // TODO IntStream mapToInt(ToIntFunction<? super T> mapper);
  // TODO LongStream mapToLong(ToLongFunction<? super T> mapper);
  // TODO DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

  /**
   * Returns a lstream consisting of the results of replacing each element of this lstream with the
   * contents of a mapped lstream produced by applying the provided mapping function to each
   * element. (If a mapped lstream is {@code null} an empty lstream is used, instead.)
   *
   * <p>This is an intermediate operation.
   *
   * @apiNote The {@code flatMap()} operation has the effect of applying a one-to-many
   *     transformation to the elements of the lstream, and then flattening the resulting elements
   *     into a new lstream.
   *     <p><b>Examples.</b>
   *     <p>If {@code orders} is a lstream of purchase orders, and each purchase order contains a
   *     collection of line items, then the following produces a lstream containing all the line
   *     items in all the orders:
   *     <pre>{@code
   * orders.flatMap(order -> order.getLineItems().stream())...
   * }</pre>
   *
   * @param <R> The element type of the new stream
   * @param mapper a function to apply to each element which produces a stream of new values
   * @return the new stream
   */
  <R> LStreamApi<R> flatMap(Function<? super T, ? extends LStreamApi<? extends R>> mapper);

  // TODO Add all the javadoc
  LStreamApi<T> distinct();

  LStreamApi<T> sorted();

  LStreamApi<T> sorted(@Nullable Comparator<? super T> comparator);

  LStreamApi<T> peek(Consumer<? super T> action);

  LStreamApi<T> limit(long maxSize);

  LStreamApi<T> skip(long nbToSkip);

  LStreamApi<T> takeWhile(Predicate<? super T> predicate);

  LStreamApi<T> takeWhilePrevious(Predicate<? super T> previousPredicate);

  LStreamApi<T> dropWhile(Predicate<? super T> predicate);

  void forEach(Consumer<? super T> action);

  Object[] toArray();

  <A> A[] toArray(IntFunction<A[]> generator);

  T reduce(T identity, BinaryOperator<T> accumulator);

  <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate);

  Optional<T> reduce(BinaryOperator<T> accumulator);

  <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator);

  <R> R collect(LCollector<? super T, ?, R> collector);

  List<T> toList();

  Optional<T> min(Comparator<? super T> comparator);

  Optional<T> max(Comparator<? super T> comparator);

  long count();

  boolean anyMatch(Predicate<? super T> predicate);

  boolean allMatch(Predicate<? super T> predicate);

  boolean noneMatch(Predicate<? super T> predicate);

  Optional<T> findFirst();

  /**
   * @throws SeveralElementsException if there are several elements left in the stream
   */
  Optional<T> findOne() throws SeveralElementsException;

  Optional<T> findLast();

  Iterator<T> iterator();
}
