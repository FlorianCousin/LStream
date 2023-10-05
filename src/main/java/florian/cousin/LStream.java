package florian.cousin;

import florian.cousin.collector.LCollector;
import florian.cousin.collector.LCollectors;
import florian.cousin.exception.SeveralElementsException;
import florian.cousin.iterator.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public abstract class LStream<T> implements Iterator<T> {

  public LStream<T> filter(Predicate<? super T> predicate) {
    return new FilterIterator<>(this, predicate);
  }

  public <R> LStream<R> map(Function<? super T, ? extends R> mapping) {
    return new MappingIterator<>(this, mapping);
  }

  public <R> LStream<R> flatMap(Function<? super T, ? extends LStream<? extends R>> mapper) {
    return new FlatMappingIterator<>(this, mapper);
  }

  public LStream<T> distinct() {
    return new DistinctIterator<>(this);
  }

  public LStream<T> sorted() {
    return sorted(null);
  }

  public LStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    return new SortedIterator<>(this, comparator);
  }

  public LStream<T> peek(Consumer<? super T> action) {
    return new PeekIterator<>(this, action);
  }

  public LStream<T> limit(long maxSize) {
    return new LimitIterator<>(this, maxSize);
  }

  public LStream<T> skip(long nbToSkip) {
    return new SkipIterator<>(this, nbToSkip);
  }

  public LStream<T> takeWhile(Predicate<? super T> predicate) {
    return new takeWhileIterator<>(this, predicate);
  }

  public LStream<T> takeWhilePrevious(Predicate<? super T> previousPredicate) {
    return new TakeWhilePreviousIterator<>(this, previousPredicate);
  }

  public LStream<T> dropWhile(Predicate<? super T> predicate) {
    return new DropWhileIterator<>(this, predicate);
  }

  public void forEach(Consumer<? super T> action) {
    while (hasNext()) {
      action.accept(next());
    }
  }

  public Object[] toArray() {
    return toList().toArray();
  }

  public <A> A[] toArray(IntFunction<A[]> generator) {
    return toList().toArray(generator);
  }

  public T reduce(T identity, BinaryOperator<T> accumulator) {
    T currentValue = identity;
    while (hasNext()) {
      currentValue = accumulator.apply(currentValue, next());
    }
    return currentValue;
  }

  public <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = aggregate.apply(currentValue, next());
    }
    return currentValue;
  }

  public Optional<T> reduce(BinaryOperator<T> accumulator) {
    Optional<T> reducedOptional = Optional.empty();
    while (hasNext()) {
      T next = next();
      T reducedValue = reducedOptional.map(value -> accumulator.apply(value, next)).orElse(next);
      reducedOptional = Optional.ofNullable(reducedValue);
    }
    return reducedOptional;
  }

  public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return collect(LCollector.of(supplier, accumulator));
  }

  public <R> R collect(LCollector<? super T, ?, R> collector) {
    return collector.collect(this);
  }

  public List<T> toList() {
    return collect(LCollectors.toUnmodifiableList());
  }

  public Optional<T> min(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMinimum =
        (previousMin, nextValue) ->
            comparator.compare(previousMin, nextValue) > 0 ? nextValue : previousMin;

    return reduce(keepMinimum);
  }

  public Optional<T> max(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMaximum =
        (previousMax, nextValue) ->
            comparator.compare(previousMax, nextValue) < 0 ? nextValue : previousMax;

    return reduce(keepMaximum);
  }

  public long count() {
    long nbElementsIterated = 0;
    while (hasNext()) {
      next();
      nbElementsIterated++;
    }
    return nbElementsIterated;
  }

  public boolean anyMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (predicate.test(next())) {
        return true;
      }
    }
    return false;
  }

  public boolean allMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (!predicate.test(next())) {
        return false;
      }
    }
    return true;
  }

  public boolean noneMatch(Predicate<? super T> predicate) {
    return !anyMatch(predicate);
  }

  public Optional<T> findFirst() {
    return hasNext() ? Optional.ofNullable(next()) : Optional.empty();
  }

  /**
   * @throws SeveralElementsException if there are several elements left in the stream
   */
  public Optional<T> findOne() throws SeveralElementsException {
    Optional<T> first = findFirst();
    if (hasNext()) {
      throw new SeveralElementsException(
          "Call to \"findOne\" but there were several elements left in the stream");
    }
    return first;
  }

  public Optional<T> findLast() {
    return reduce((first, second) -> second);
  }

  public static <T> LStream.Builder<T> builder() {
    return new Builder<>();
  }

  public static <T> LStream<T> empty() {
    return new SimpleIterator<>(Collections.emptyIterator());
  }

  public static <T> LStream<T> from(Iterable<T> iterable) {
    return new SimpleIterator<>(iterable.iterator());
  }

  @SafeVarargs
  public static <T> LStream<T> of(T... iterationObjects) {
    return new SimpleIterator<>(Arrays.asList(iterationObjects).iterator());
  }

  public static <T> LStream<T> iterate(final T initialValue, final UnaryOperator<T> computeNextValue) {
    return iterate(initialValue, value -> true, computeNextValue);
  }

  public static <T> LStream<T> iterate(
          T initialValue, Predicate<? super T> hasNext, UnaryOperator<T> next) {
    return new IterateIterator<>(initialValue, hasNext, next);
  }

  public static <T> LStream<T> generate(Supplier<? extends T> nextValueGenerator) {
    return generate(nextValueGenerator, value -> true);
  }

  public static <T> LStream<T> generate(
          Supplier<? extends T> nextValueGenerator, Predicate<? super T> hasNext) {
    return new GenerateIterator<>(nextValueGenerator, hasNext);
  }

  @SafeVarargs
  public static <T> LStream<T> concat(LStream<? extends T>... lStreams) {
    return LStream.of(lStreams).flatMap(Function.identity());
  }

  public static class Builder<T> implements Consumer<T> {

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
