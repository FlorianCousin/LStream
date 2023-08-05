package florian.cousin.collector;

import florian.cousin.LinearStream;
import florian.cousin.utils.KahanSummationLong;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public final class LinearCollectors {

  // TODO Replace all Atomic usage to int[] or some custom record

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

  public static <T> LinearCollector<T, AtomicLong, Long> counting() {
    BiConsumer<AtomicLong, ? super T> accumulator =
        (accumulation, newValue) -> accumulation.incrementAndGet();
    return LinearCollector.of(AtomicLong::new, accumulator, AtomicLong::get);
  }

  public static <T> LinearCollector<T, AtomicReference<T>, Optional<T>> minBy(
      // TODO Autoriser Comparator<? super T>
      Comparator<T> comparator) {
    return LinearCollector.of(
        AtomicReference::new,
        (previousMin, newValue) -> {
          T newMin =
              previousMin.get() == null
                  ? newValue
                  : BinaryOperator.minBy(comparator).apply(previousMin.get(), newValue);
          previousMin.set(newMin);
        },
        atomicMin -> Optional.ofNullable(atomicMin.get()));
  }

  public static <T> LinearCollector<T, AtomicReference<T>, Optional<T>> maxBy(
      // TODO Autoriser Comparator<? super T>
      Comparator<T> comparator) {
    return LinearCollector.of(
        AtomicReference::new,
        (atomicMax, newValue) -> {
          T newMax =
              atomicMax.get() == null
                  ? newValue
                  : BinaryOperator.maxBy(comparator).apply(atomicMax.get(), newValue);
          atomicMax.set(newMax);
        },
        atomicMax -> Optional.ofNullable(atomicMax.get()));
  }

  public static <T> LinearCollector<T, AtomicInteger, Integer> summingInt(
      ToIntFunction<? super T> mapper) {
    return LinearCollector.of(
        AtomicInteger::new,
        (atomicSum, newValue) -> atomicSum.addAndGet(mapper.applyAsInt(newValue)),
        AtomicInteger::get);
  }

  public static <T> LinearCollector<T, long[], Long> summingLong(ToLongFunction<? super T> mapper) {
    return LinearCollector.of(
        () -> new long[1],
        (currentSum, newValue) -> currentSum[0] += mapper.applyAsLong(newValue),
        sum -> sum[0]);
  }

  public static <T> LinearCollector<T, KahanSummationLong, Double> summingDouble(
      ToDoubleFunction<? super T> mapper) {
    return LinearCollector.of(
        KahanSummationLong::new,
        (kahan, newValue) -> kahan.add(mapper.applyAsDouble(newValue)),
        KahanSummationLong::sum);
  }
}
