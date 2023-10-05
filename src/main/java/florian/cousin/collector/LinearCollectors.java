package florian.cousin.collector;

import florian.cousin.LinearStream;
import florian.cousin.utils.*;
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
      Comparator<? super T> comparator) {
    return LinearCollector.of(
        HeapReference::new,
        (previousMin, newValue) -> accumulateMin(comparator, previousMin, newValue),
        atomicMin -> Optional.ofNullable(atomicMin.value()));
  }

  private static <T> void accumulateMin(
      Comparator<? super T> comparator, HeapReference<T> previousMin, T newValue) {
    T previousMinValue = previousMin.value();
    BinaryOperator<T> compareOperator = BinaryOperator.minBy(comparator);
    T newMin =
        previousMinValue == null ? newValue : compareOperator.apply(previousMinValue, newValue);
    previousMin.value(newMin);
  }

  public static <T> LinearCollector<T, HeapReference<T>, Optional<T>> maxBy(
      Comparator<? super T> comparator) {
    return LinearCollector.of(
        HeapReference::new,
        (currentMax, newValue) -> accumulateMax(comparator, currentMax, newValue),
        atomicMax -> Optional.ofNullable(atomicMax.value()));
  }

  private static <T> void accumulateMax(
      Comparator<? super T> comparator, HeapReference<T> currentMax, T newValue) {
    T previousMaxValue = currentMax.value();
    BinaryOperator<T> compareOperator = BinaryOperator.maxBy(comparator);
    T newMax =
        previousMaxValue == null ? newValue : compareOperator.apply(previousMaxValue, newValue);
    currentMax.value(newMax);
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

  /**
   * Returns a {@link LinearCollector} that produces the arithmetic mean of an integer-valued
   * function applied to the input elements. If no elements are present, the result is 0.
   */
  public static <T> LinearCollector<T, AverageLong, Double> averagingInt(
      ToIntFunction<? super T> mapper) {
    return averagingLong(mapper::applyAsInt);
  }

  /**
   * Returns a {@link LinearCollector} that produces the arithmetic mean of a long-valued function
   * applied to the input elements. If no elements are present, the result is 0.
   */
  public static <T> LinearCollector<T, AverageLong, Double> averagingLong(
      ToLongFunction<? super T> mapper) {
    return LinearCollector.of(
        AverageLong::new,
        (averageLong, newValue) -> averageLong.addValue(mapper.applyAsLong(newValue)),
        AverageLong::getAverage);
  }

  /**
   * Returns a collector that produces the arithmetic mean of a double-valued function applied to
   * {@link LinearStream} values.
   *
   * <p>The sum is first calculated one by one.
   *
   * <p>Consequently the order of the {@link LinearStream} is important : averaging double of 1e300,
   * 3.5, -1e300 is 0 but averaging double of 1e300, -1e300, 3 is 1.
   */
  public static <T> LinearCollector<T, AverageDouble, Double> averagingDouble(
      ToDoubleFunction<? super T> mapper) {
    return LinearCollector.of(
        AverageDouble::new,
        (averageDouble, newValue) -> averageDouble.addValue(mapper.applyAsDouble(newValue)),
        AverageDouble::getAverage);
  }

  public static <T, K, U> LinearCollector<T, Map<K, U>, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return LinearCollector.of(
        HashMap::new,
        (currentMap, newValue) -> {
          K key = keyMapper.apply(newValue);
          U value = valueMapper.apply(newValue);
          if (currentMap.containsKey(key)) {
            throw duplicateKeyException(key, currentMap.get(key), value);
          }
          currentMap.put(key, value);
        });
  }

  public static <T, K, U> LinearCollector<T, Map<K, U>, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
  }

  public static <T, K, U, M extends Map<K, U>> LinearCollector<T, M, M> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      Supplier<M> mapFactory) {

    BiConsumer<M, T> accumulator =
        (currentMap, newValue) -> {
          K key = keyMapper.apply(newValue);
          U value = valueMapper.apply(newValue);
          if (currentMap.containsKey(key)) {
            throw duplicateKeyException(key, currentMap.get(key), value);
          }
          currentMap.put(key, value);
        };

    return LinearCollector.of(mapFactory, accumulator);
  }

  public static <T, K, U, M extends Map<K, U>> LinearCollector<T, M, M> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction,
      Supplier<M> mapFactory) {

    BiConsumer<M, T> accumulator =
        (currentMap, newValue) -> {
          K key = keyMapper.apply(newValue);
          U value = valueMapper.apply(newValue);
          currentMap.merge(key, value, mergeFunction);
        };

    return LinearCollector.of(mapFactory, accumulator);
  }

  private static <K, U> IllegalStateException duplicateKeyException(
      K key, U currentValue, U valueToAdd) {
    return new IllegalStateException(
        "Duplicate key %s (attempted to add value %s but %s already existed)"
            .formatted(key, valueToAdd, currentValue));
  }

  public static <T, K, U> LinearCollector<T, Map<K, U>, Map<K, U>> toUnmodifiableMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return collectingAndThen(toMap(keyMapper, valueMapper), Collections::unmodifiableMap);
  }

  public static <T, K, U> LinearCollector<T, Map<K, U>, Map<K, U>> toUnmodifiableMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return collectingAndThen(
        toMap(keyMapper, valueMapper, mergeFunction), Collections::unmodifiableMap);
  }

  public static <T, K>
      LinearCollector<T, Map<K, LinearStream.Builder<T>>, Map<K, List<T>>> groupingBy(
          Function<? super T, ? extends K> classifier) {
    return groupingBy(classifier, toList());
  }

  public static <T, K, A, D>
      LinearCollector<T, Map<K, LinearStream.Builder<T>>, Map<K, D>> groupingBy(
          Function<? super T, ? extends K> classifier,
          LinearCollector<? super T, A, D> downstream) {
    return groupingBy(classifier, HashMap::new, downstream);
  }

  public static <T, K, D, M extends Map<K, D>>
      LinearCollector<T, Map<K, LinearStream.Builder<T>>, M> groupingBy(
          Function<? super T, ? extends K> classifier,
          Supplier<M> mapFactory,
          LinearCollector<? super T, ?, D> downstream) {

    Map<K, LinearStream.Builder<T>> accumulator = new HashMap<>();

    return LinearCollector.of(
        () -> accumulator,
        (accumulation, newValue) -> {
          K newValueKey = classifier.apply(newValue);
          mergeNewValue(accumulation, newValueKey, newValue);
        },
        accumulation -> finish(mapFactory, downstream, accumulation));
  }

  private static <T, K> void mergeNewValue(
      Map<K, LinearStream.Builder<T>> accumulation, K newValueKey, T newValue) {
    if (accumulation.containsKey(newValueKey)) {
      accumulation.get(newValueKey).accept(newValue);
    } else {
      accumulation.put(newValueKey, LinearStream.<T>builder().add(newValue));
    }
  }

  private static <T, K, D, M extends Map<K, D>> M finish(
      Supplier<M> mapFactory,
      LinearCollector<? super T, ?, D> downstream,
      Map<K, LinearStream.Builder<T>> accumulation) {

    return LinearStream.from(accumulation.entrySet())
        .collect(
            LinearCollectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().build().collect(downstream),
                mapFactory));
  }

  public static <T>
      LinearCollector<T, Map<Boolean, LinearStream.Builder<T>>, Map<Boolean, List<T>>>
          partitioningBy(Predicate<? super T> predicate) {
    return groupingBy(predicate::test);
  }

  public static <T, D, A>
      LinearCollector<T, Map<Boolean, LinearStream.Builder<T>>, Map<Boolean, D>> partitioningBy(
          Predicate<? super T> predicate, LinearCollector<? super T, A, D> downstream) {
    return groupingBy(predicate::test, downstream);
  }

  // TODO toConcurrentMap ?
  //  LStream is always sequential, and if we need a concurrentMap, we can give it as mapFactory
}
