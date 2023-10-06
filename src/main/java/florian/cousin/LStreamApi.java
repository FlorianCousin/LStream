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

  // TODO IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);
  // TODO LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);
  // TODO DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

  /**
   * Returns a lstream consisting of the distinct elements (according to {@link
   * Object#equals(Object)}) of this lstream.
   *
   * <p>the selection of distinct elements is stable (for duplicated elements, the element appearing
   * first in the encounter order is preserved.).
   *
   * <p>This is a stateful intermediate operation.
   *
   * @return the new stream
   */
  LStreamApi<T> distinct();

  /**
   * Returns a lstream consisting of the elements of this lstream, sorted according to natural
   * order. If the elements of this stream are not {@code Comparable}, a {@code
   * java.lang.ClassCastException} may be thrown when the terminal operation is executed.
   *
   * <p>This is a stateful intermediate operation.
   *
   * @return the new stream
   */
  LStreamApi<T> sorted();

  /**
   * Returns a lstream consisting of the elements of this lstream, sorted according to the provided
   * {@code Comparator}. If the input {@code Comparator} is {@code null}, the natural comparator is
   * used.
   *
   * <p>This is a stateful intermediate operation.
   *
   * @param comparator a {@code Comparator} to be used to compare stream elements
   * @return the new stream
   */
  LStreamApi<T> sorted(@Nullable Comparator<? super T> comparator);

  /**
   * Returns a lstream consisting of the elements of this lstream, additionally performing the
   * provided action on each element as elements are consumed from the resulting stream.
   *
   * <p>This is an intermediate operation.
   *
   * <p>In cases where the lstream implementation is able to optimize away the production of some or
   * all the elements (such as with short-circuiting operations like {@code findFirst}), the action
   * will not be invoked for those elements.
   *
   * @param action an action to perform on the elements as they are consumed from the stream
   * @return the new stream
   */
  LStreamApi<T> peek(Consumer<? super T> action);

  /**
   * Returns a lstream consisting of the elements of this lstream, truncated to be no longer than
   * {@code maxSize} in length.
   *
   * <p>This is a short-circuiting stateful intermediate operation.
   *
   * @param maxSize the number of elements the stream should be limited to
   * @return the new stream
   */
  LStreamApi<T> limit(long maxSize);

  /**
   * Returns a lstream consisting of the remaining elements of this lstream
   * after discarding the first {@code nbToSkip} elements of the lstream.
   * If this lstream contains fewer than {@code nbToSkip} elements then an
   * empty stream will be returned.
   *
   * <p>This is a stateful
   * intermediate operation.
   *
   * @param nbToSkip the number of leading elements to skip
   * @return the new lstream
   * @throws IllegalArgumentException if {@code n} is negative
   */
  LStreamApi<T> skip(long nbToSkip);

  // TODO Add all the javadoc
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
