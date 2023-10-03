package florian.cousin.collector;

import florian.cousin.LinearStream;
import florian.cousin.utils.HeapInteger;
import florian.cousin.utils.HeapLong;
import florian.cousin.utils.HeapReference;
import java.util.*;
import java.util.function.*;

public final class LinearCollectors {

  private LinearCollectors() {
    throw new IllegalStateException("This is a utility class.");
  }

  public static <T, C extends Collection<T>> LinearCollector<T, C, C> toCollection(
      Supplier<C> collectionFactory) {
    return LinearCollector.of(collectionFactory, Collection::add);
  }

  public static <T> LinearCollector<T, List<T>, List<T>> toList() {
    return LinearCollector.of(ArrayList::new, List::add);
  }

  public static <T> LinearCollector<T, List<T>, List<T>> toUnmodifiableList() {
    return LinearCollector.of(ArrayList::new, List::add, Collections::unmodifiableList);
  }

  public static <T> LinearCollector<T, Set<T>, Set<T>> toSet() {
    return LinearCollector.of(HashSet::new, Set::add);
  }

  public static <T> LinearCollector<T, Set<T>, Set<T>> toUnmodifiableSet() {
    return LinearCollector.of(HashSet::new, Set::add, Collections::unmodifiableSet);
  }

  public static LinearCollector<CharSequence, StringBuilder, String> joining() {
    return LinearCollector.of(StringBuilder::new, StringBuilder::append, StringBuilder::toString);
  }

  public static LinearCollector<CharSequence, StringJoiner, String> joining(
      CharSequence delimiter) {
    return joining(delimiter, "", "");
  }

  public static LinearCollector<CharSequence, StringJoiner, String> joining(
      CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return LinearCollector.of(
        () -> new StringJoiner(delimiter, prefix, suffix),
        StringJoiner::add,
        StringJoiner::toString);
  }

  public static <T, U, A, R> LinearCollector<T, A, R> mapping(
      Function<? super T, ? extends U> mapper, LinearCollector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, T> newAccumulator = (a, u) -> downstreamAccumulator.accept(a, mapper.apply(u));
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, U, A, R> LinearCollector<T, A, R> flatMapping(
      Function<? super T, ? extends LinearStream<? extends U>> mapper,
      LinearCollector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, ? super T> newAccumulator =
        (accumulation, newValue) -> {
          LinearStream<? extends U> flatValues = mapper.apply(newValue);
          flatValues.forEach(flatValue -> downstreamAccumulator.accept(accumulation, flatValue));
        };
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, A, R> LinearCollector<T, A, R> filtering(
      Predicate<? super T> predicate, LinearCollector<? super T, A, R> downstream) {
    BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, ? super T> newAccumulator =
        (accumulation, newValue) -> {
          if (predicate.test(newValue)) {
            downstreamAccumulator.accept(accumulation, newValue);
          }
        };
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, A, R, S> LinearCollector<T, A, S> collectingAndThen(
      LinearCollector<T, A, R> downstream, Function<R, S> finisher) {
    return downstream.collectingAndThen(finisher);
  }

  public static <T> LinearCollector<T, HeapLong, Long> counting() {
    BiConsumer<HeapLong, ? super T> accumulator = (accumulation, newValue) -> accumulation.add(1);
    return LinearCollector.of(HeapLong::new, accumulator, HeapLong::value);
  }

  public static <T> LinearCollector<T, HeapReference<T>, Optional<T>> minBy(
      // TODO Autoriser Comparator<? super T>
      Comparator<T> comparator) {
    // TODO Faire des sous-fonctions
    return LinearCollector.of(
        HeapReference::new,
        (previousMin, newValue) -> {
          T newMin =
              previousMin.value() == null
                  ? newValue
                  : BinaryOperator.minBy(comparator).apply(previousMin.value(), newValue);
          previousMin.value(newMin);
        },
        atomicMin -> Optional.ofNullable(atomicMin.value()));
  }

  public static <T> LinearCollector<T, HeapReference<T>, Optional<T>> maxBy(
      // TODO Autoriser Comparator<? super T>
      Comparator<T> comparator) {
    // TODO Faire des sous-fonctions
    return LinearCollector.of(
        HeapReference::new,
        (currentMax, newValue) -> {
          T newMax =
              currentMax.value() == null
                  ? newValue
                  : BinaryOperator.maxBy(comparator).apply(currentMax.value(), newValue);
          currentMax.value(newMax);
        },
        atomicMax -> Optional.ofNullable(atomicMax.value()));
  }

  public static <T> LinearCollector<T, HeapInteger, Integer> summingInt(
      ToIntFunction<? super T> mapper) {
    return LinearCollector.of(
        HeapInteger::new,
        (currentSum, newValue) -> currentSum.add(mapper.applyAsInt(newValue)),
        HeapInteger::value);
  }

  public static <T> LinearCollector<T, HeapLong, Long> summingLong(
      ToLongFunction<? super T> mapper) {
    return LinearCollector.of(
        HeapLong::new,
        (currentSum, newValue) -> currentSum.add(mapper.applyAsLong(newValue)),
        HeapLong::value);
  }

  /**
   * Creates a collector that sums all values of a {@link LinearStream} one by one.
   *
   * <p>The order of the {@link LinearStream} is important : summing double of 1e300, 3.5, -1e300 is
   * 0 but summing double of 1e300, -1e300, 3.5 is 3.5.
   */
  public static <T> LinearCollector<T, double[], Double> summingDouble(
      ToDoubleFunction<? super T> mapper) {
    return LinearCollector.of(
        () -> new double[1],
        (currentSum, newValue) -> currentSum[0] += mapper.applyAsDouble(newValue),
        sum -> sum[0]);
  }
}
