package florian.cousin;

import florian.cousin.collector.LCollector;
import florian.cousin.exception.SeveralElementsException;
import florian.cousin.iterator.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public interface LStream<T> extends Iterator<T> {

  default LStream<T> filter(Predicate<? super T> predicate) {
    return new FilterIterator<>(this, predicate);
  }

  default <R> LStream<R> map(Function<? super T, ? extends R> mapping) {
    return new MappingIterator<>(this, mapping);
  }

  default <R> LStream<R> flatMap(Function<? super T, ? extends LStream<? extends R>> mapper) {
    return new FlatMappingIterator<>(this, mapper);
  }

  default LStream<T> distinct() {
    return new DistinctIterator<>(this);
  }

  default LStream<T> sorted() {
    return sorted(null);
  }

  default LStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    return new SortedIterator<>(this, comparator);
  }

  default LStream<T> peek(Consumer<? super T> action) {
    return new PeekIterator<>(this, action);
  }

  default LStream<T> limit(long maxSize) {
    return new LimitIterator<>(this, maxSize);
  }

  default LStream<T> skip(long nbToSkip) {
    return new SkipIterator<>(this, nbToSkip);
  }

  default LStream<T> takeWhile(Predicate<? super T> predicate) {
    return new takeWhileIterator<>(this, predicate);
  }

  default LStream<T> takeWhilePrevious(Predicate<? super T> previousPredicate) {
    return new TakeWhilePreviousIterator<>(this, previousPredicate);
  }

  default LStream<T> dropWhile(Predicate<? super T> predicate) {
    return new DropWhileIterator<>(this, predicate);
  }

  default void forEach(Consumer<? super T> action) {
    while (hasNext()) {
      action.accept(next());
    }
  }

  default Object[] toArray() {
    return toList().toArray();
  }

  default <A> A[] toArray(IntFunction<A[]> generator) {
    return toList().toArray(generator);
  }

  default T reduce(T identity, BinaryOperator<T> accumulator) {
    T currentValue = identity;
    while (hasNext()) {
      currentValue = accumulator.apply(currentValue, next());
    }
    return currentValue;
  }

  default <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = aggregate.apply(currentValue, next());
    }
    return currentValue;
  }

  default Optional<T> reduce(BinaryOperator<T> accumulator) {
    Optional<T> reducedOptional = Optional.empty();
    while (hasNext()) {
      T next = next();
      T reducedValue = reducedOptional.map(value -> accumulator.apply(value, next)).orElse(next);
      reducedOptional = Optional.ofNullable(reducedValue);
    }
    return reducedOptional;
  }

  default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return collect(LCollector.of(supplier, accumulator));
  }

  default <R> R collect(LCollector<? super T, ?, R> collector) {
    return collector.collect(this);
  }

  default List<T> toList() {
    return collect(
        LCollector.<T, List<T>, List<T>>of(
            ArrayList::new, List::add, Collections::unmodifiableList));
  }

  default Optional<T> min(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMinimum =
        (previousMin, nextValue) ->
            comparator.compare(previousMin, nextValue) > 0 ? nextValue : previousMin;

    return reduce(keepMinimum);
  }

  default Optional<T> max(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMaximum =
        (previousMax, nextValue) ->
            comparator.compare(previousMax, nextValue) < 0 ? nextValue : previousMax;

    return reduce(keepMaximum);
  }

  default long count() {
    long nbElementsIterated = 0;
    while (hasNext()) {
      next();
      nbElementsIterated++;
    }
    return nbElementsIterated;
  }

  default boolean anyMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (predicate.test(next())) {
        return true;
      }
    }
    return false;
  }

  default boolean allMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (!predicate.test(next())) {
        return false;
      }
    }
    return true;
  }

  default boolean noneMatch(Predicate<? super T> predicate) {
    return !anyMatch(predicate);
  }

  default Optional<T> findFirst() {
    return hasNext() ? Optional.ofNullable(next()) : Optional.empty();
  }

  /**
   * @throws SeveralElementsException if there are several elements left in the stream
   */
  default Optional<T> findOne() throws SeveralElementsException {
    Optional<T> first = findFirst();
    if (hasNext()) {
      throw new SeveralElementsException(
          "Call to \"findOne\" but there were several elements left in the stream");
    }
    return first;
  }

  default Optional<T> findLast() {
    return reduce((first, second) -> second);
  }

  static <T> LStream.Builder<T> builder() {
    return new Builder<>();
  }

  static <T> LStream<T> empty() {
    return new SimpleIterator<>(Collections.emptyIterator());
  }

  static <T> LStream<T> from(Iterable<T> iterable) {
    return new SimpleIterator<>(iterable.iterator());
  }

  @SafeVarargs
  static <T> LStream<T> of(T... iterationObjects) {
    return new SimpleIterator<>(Arrays.asList(iterationObjects).iterator());
  }

  static <T> LStream<T> iterate(final T initialValue, final UnaryOperator<T> computeNextValue) {
    return iterate(initialValue, value -> true, computeNextValue);
  }

  static <T> LStream<T> iterate(
      T initialValue, Predicate<? super T> hasNext, UnaryOperator<T> next) {
    return new IterateIterator<>(initialValue, hasNext, next);
  }

  static <T> LStream<T> generate(Supplier<? extends T> nextValueGenerator) {
    return generate(nextValueGenerator, value -> true);
  }

  static <T> LStream<T> generate(
      Supplier<? extends T> nextValueGenerator, Predicate<? super T> hasNext) {
    return new GenerateIterator<>(nextValueGenerator, hasNext);
  }

  @SafeVarargs
  static <T> LStream<T> concat(LStream<? extends T>... lStreams) {
    return LStream.of(lStreams).flatMap(Function.identity());
  }

  class Builder<T> implements Consumer<T> {

    private final List<T> values = new LinkedList<>();

    @Override
    public void accept(T t) {
      values.add(t);
    }

    public LStream.Builder<T> add(T t) {
      accept(t);
      return this;
    }

    public LStream<T> build() {
      return LStream.from(values);
    }
  }
}
