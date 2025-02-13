package cousin.florian.collector;

import cousin.florian.LStream;
import cousin.florian.utils.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;
import org.jetbrains.annotations.Contract;

public final class LCollectors {

  private LCollectors() {
    throw new IllegalStateException("This is a utility class.");
  }

  public static <T, C extends Collection<T>> LCollector<T, C, C> toCollection(
      Supplier<C> collectionFactory) {
    return LCollector.of(collectionFactory, Collection::add);
  }

  public static <T> LCollector<T, List<T>, List<T>> toList() {
    return LCollector.of(ArrayList::new, List::add);
  }

  public static <T> LCollector<T, List<T>, List<T>> toUnmodifiableList() {
    return LCollector.of(ArrayList::new, List::add, Collections::unmodifiableList);
  }

  public static <T> LCollector<T, Set<T>, Set<T>> toSet() {
    return LCollector.of(HashSet::new, Set::add);
  }

  public static <T> LCollector<T, Set<T>, Set<T>> toUnmodifiableSet() {
    return LCollector.of(HashSet::new, Set::add, Collections::unmodifiableSet);
  }

  public static LCollector<CharSequence, StringBuilder, String> joining() {
    return LCollector.of(StringBuilder::new, StringBuilder::append, StringBuilder::toString);
  }

  public static LCollector<CharSequence, StringJoiner, String> joining(CharSequence delimiter) {
    return joining(delimiter, "", "");
  }

  public static LCollector<CharSequence, StringJoiner, String> joining(
      CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return LCollector.of(
        () -> new StringJoiner(delimiter, prefix, suffix),
        StringJoiner::add,
        StringJoiner::toString);
  }

  public static <T, U, A, R> LCollector<T, A, R> mapping(
      Function<? super T, ? extends U> mapper, LCollector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, T> newAccumulator = (a, u) -> downstreamAccumulator.accept(a, mapper.apply(u));
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, U, A, R> LCollector<T, A, R> flatMapping(
      Function<? super T, ? extends LStream<? extends U>> mapper,
      LCollector<? super U, A, R> downstream) {
    BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, ? super T> newAccumulator =
        (accumulation, newValue) -> {
          LStream<? extends U> flatValues = mapper.apply(newValue);
          flatValues.forEach(flatValue -> downstreamAccumulator.accept(accumulation, flatValue));
        };
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, A, R> LCollector<T, A, R> filtering(
      Predicate<? super T> predicate, LCollector<? super T, A, R> downstream) {
    BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
    BiConsumer<A, ? super T> newAccumulator =
        (accumulation, newValue) -> {
          if (predicate.test(newValue)) {
            downstreamAccumulator.accept(accumulation, newValue);
          }
        };
    return downstream.withAccumulator(newAccumulator);
  }

  public static <T, A, R, S> LCollector<T, A, S> collectingAndThen(
      LCollector<T, A, R> downstream, Function<R, S> finisher) {
    return downstream.collectingAndThen(finisher);
  }

  public static <T> LCollector<T, HeapLong, Long> counting() {
    BiConsumer<HeapLong, ? super T> accumulator = (accumulation, newValue) -> accumulation.add(1);
    return LCollector.of(HeapLong::new, accumulator, HeapLong::value);
  }

  public static <T> LCollector<T, HeapReference<T>, Optional<T>> minBy(
      Comparator<? super T> comparator) {
    return LCollector.of(
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

  public static <T> LCollector<T, HeapReference<T>, Optional<T>> maxBy(
      Comparator<? super T> comparator) {
    return LCollector.of(
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

  public static <T> LCollector<T, HeapInteger, Integer> summingInt(
      ToIntFunction<? super T> mapper) {
    return LCollector.of(
        HeapInteger::new,
        (currentSum, newValue) -> currentSum.add(mapper.applyAsInt(newValue)),
        HeapInteger::value);
  }

  public static <T> LCollector<T, HeapLong, Long> summingLong(ToLongFunction<? super T> mapper) {
    return LCollector.of(
        HeapLong::new,
        (currentSum, newValue) -> currentSum.add(mapper.applyAsLong(newValue)),
        HeapLong::value);
  }

  /**
   * Creates a collector that sums all values of a {@link LStream} one by one.
   *
   * <p>The order of the {@link LStream} is important : summing double of 1e300, 3.5, -1e300 is 0
   * but summing double of 1e300, -1e300, 3.5 is 3.5.
   */
  public static <T> LCollector<T, double[], Double> summingDouble(
      ToDoubleFunction<? super T> mapper) {
    return LCollector.of(
        () -> new double[1],
        (currentSum, newValue) -> currentSum[0] += mapper.applyAsDouble(newValue),
        sum -> sum[0]);
  }

  /**
   * Returns a {@link LCollector} that produces the arithmetic mean of an integer-valued function
   * applied to the input elements. If no elements are present, the result is 0.
   */
  public static <T> LCollector<T, AverageLong, Double> averagingInt(
      ToIntFunction<? super T> mapper) {
    return averagingLong(mapper::applyAsInt);
  }

  /**
   * Returns a {@link LCollector} that produces the arithmetic mean of a long-valued function
   * applied to the input elements. If no elements are present, the result is 0.
   */
  public static <T> LCollector<T, AverageLong, Double> averagingLong(
      ToLongFunction<? super T> mapper) {
    return LCollector.of(
        AverageLong::new,
        (averageLong, newValue) -> averageLong.addValue(mapper.applyAsLong(newValue)),
        AverageLong::getAverage);
  }

  /**
   * Returns a collector that produces the arithmetic mean of a double-valued function applied to
   * {@link LStream} values.
   *
   * <p>The sum is first calculated one by one.
   *
   * <p>Consequently the order of the {@link LStream} is important : averaging double of 1e300, 3.5,
   * -1e300 is 0 but averaging double of 1e300, -1e300, 3 is 1.
   */
  public static <T> LCollector<T, AverageDouble, Double> averagingDouble(
      ToDoubleFunction<? super T> mapper) {
    return LCollector.of(
        AverageDouble::new,
        (averageDouble, newValue) -> averageDouble.addValue(mapper.applyAsDouble(newValue)),
        AverageDouble::getAverage);
  }

  public static <T, K, U> LCollector<T, Map<K, U>, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return LCollector.of(
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

  public static <T, K, U> LCollector<T, Map<K, U>, Map<K, U>> toMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
  }

  public static <T, K, U, M extends Map<K, U>> LCollector<T, M, M> toMap(
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

    return LCollector.of(mapFactory, accumulator);
  }

  public static <T, K, U, M extends Map<K, U>> LCollector<T, M, M> toMap(
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

    return LCollector.of(mapFactory, accumulator);
  }

  private static <K, U> IllegalStateException duplicateKeyException(
      K key, U currentValue, U valueToAdd) {
    return new IllegalStateException(
        "Duplicate key %s (attempted to add value %s but %s already existed)"
            .formatted(key, valueToAdd, currentValue));
  }

  public static <T, K, U> LCollector<T, Map<K, U>, Map<K, U>> toUnmodifiableMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return collectingAndThen(toMap(keyMapper, valueMapper), Collections::unmodifiableMap);
  }

  public static <T, K, U> LCollector<T, Map<K, U>, Map<K, U>> toUnmodifiableMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return collectingAndThen(
        toMap(keyMapper, valueMapper, mergeFunction), Collections::unmodifiableMap);
  }

  public static <T, K, U> LCollector<T, ConcurrentMap<K, U>, ConcurrentMap<K, U>> toConcurrentMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return toMap(keyMapper, valueMapper, () -> new ConcurrentHashMap<>());
  }

  public static <T, K, U> LCollector<T, ConcurrentMap<K, U>, ConcurrentMap<K, U>> toConcurrentMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper,
      BinaryOperator<U> mergeFunction) {
    return toMap(keyMapper, valueMapper, mergeFunction, ConcurrentHashMap::new);
  }

  public static <T, K> LCollector<T, Map<K, LStream.Builder<T>>, Map<K, List<T>>> groupingBy(
      Function<? super T, ? extends K> classifier) {
    return groupingBy(classifier, toList());
  }

  public static <T, K, A, D> LCollector<T, Map<K, LStream.Builder<T>>, Map<K, D>> groupingBy(
      Function<? super T, ? extends K> classifier, LCollector<? super T, A, D> downstream) {
    return groupingBy(classifier, HashMap::new, downstream);
  }

  public static <T, K, D, M extends Map<K, D>>
      LCollector<T, Map<K, LStream.Builder<T>>, M> groupingBy(
          Function<? super T, ? extends K> classifier,
          Supplier<M> mapFactory,
          LCollector<? super T, ?, D> downstream) {

    Map<K, LStream.Builder<T>> accumulator = new HashMap<>();

    return LCollector.of(
        () -> accumulator,
        (accumulation, newValue) -> {
          K newValueKey = classifier.apply(newValue);
          mergeNewValue(accumulation, newValueKey, newValue);
        },
        accumulation -> finish(mapFactory, downstream, accumulation));
  }

  private static <T, K> void mergeNewValue(
      Map<K, LStream.Builder<T>> accumulation, K newValueKey, T newValue) {
    if (accumulation.containsKey(newValueKey)) {
      accumulation.get(newValueKey).accept(newValue);
    } else {
      accumulation.put(newValueKey, LStream.<T>builder().add(newValue));
    }
  }

  private static <T, K, D, M extends Map<K, D>> M finish(
      Supplier<M> mapFactory,
      LCollector<? super T, ?, D> downstream,
      Map<K, LStream.Builder<T>> accumulation) {

    return LStream.from(accumulation.entrySet())
        .collect(
            LCollectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().build().collect(downstream),
                mapFactory));
  }

  public static <T>
      LCollector<T, Map<Boolean, LStream.Builder<T>>, Map<Boolean, List<T>>> partitioningBy(
          Predicate<? super T> predicate) {
    return groupingBy(predicate::test);
  }

  public static <T, D, A>
      LCollector<T, Map<Boolean, LStream.Builder<T>>, Map<Boolean, D>> partitioningBy(
          Predicate<? super T> predicate, LCollector<? super T, A, D> downstream) {
    return groupingBy(predicate::test, downstream);
  }

  public static <T> LCollector<T, IntSummaryStatistics, IntSummaryStatistics> summarizingInt(
      ToIntFunction<? super T> mapper) {
    return LCollector.of(
        IntSummaryStatistics::new,
        (statistics, newValue) -> statistics.accept(mapper.applyAsInt(newValue)));
  }

  public static <T> LCollector<T, LongSummaryStatistics, LongSummaryStatistics> summarizingLong(
      ToLongFunction<? super T> mapper) {
    return LCollector.of(
        LongSummaryStatistics::new,
        (statistics, newValue) -> statistics.accept(mapper.applyAsLong(newValue)));
  }

  public static <T>
      LCollector<T, DoubleSummaryStatistics, DoubleSummaryStatistics> summarizingDouble(
          ToDoubleFunction<? super T> mapper) {
    return LCollector.of(
        DoubleSummaryStatistics::new,
        (statistics, newValue) -> statistics.accept(mapper.applyAsDouble(newValue)));
  }

  public static <T, A1, R1, R2, R> LCollector<T, A1, R> teeing(
      LCollector<? super T, A1, R1> downstream1,
      LCollector<? super T, ?, R2> downstream2,
      BiFunction<? super R1, ? super R2, R> merger) {

    LStream.Builder<T> builderForLStream2 = LStream.builder();

    UnaryOperator<T> acceptValueInBuilder = t -> acceptValueInBuilder(t, builderForLStream2);

    return mapping(acceptValueInBuilder, downstream1)
        .collectingAndThen(
            r1 -> {
              R2 r2 = builderForLStream2.build().collect(downstream2);
              return merger.apply(r1, r2);
            });
  }

  @Contract("_, _ -> param1")
  private static <T> T acceptValueInBuilder(T t, LStream.Builder<T> builder) {
    builder.accept(t);
    return t;
  }
}
