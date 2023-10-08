package florian.cousin;

import florian.cousin.collector.LCollector;
import florian.cousin.collector.LCollectors;
import florian.cousin.exception.SeveralElementsException;
import florian.cousin.iterator.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public abstract class LStream<T> implements Iterator<T>, LStreamApi<T> {

  // TODO Add sized LStream and use new ArrayList<>(size) in supplier for collector, and optimize
  //  toArray

  @Override
  public LStream<T> filter(Predicate<? super T> predicate) {
    return new FilterLStream<>(this, predicate);
  }

  @Override
  public <R> LStream<R> map(Function<? super T, ? extends R> mapper) {
    return new MappingLstream<>(this, mapper);
  }

  @Override
  public <R> LStream<R> flatMap(Function<? super T, ? extends LStreamApi<? extends R>> mapper) {
    return new FlatMappingLStream<>(this, mapper);
  }

  @Override
  public LStream<T> distinct() {
    Set<T> alreadyIteratedElements = new HashSet<>();
    return filter(alreadyIteratedElements::add);
  }

  @Override
  public LStream<T> sorted() {
    return sorted(null);
  }

  @Override
  public LStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    return new SortedLStream<>(this, comparator);
  }

  @Override
  public LStream<T> peek(Consumer<? super T> action) {
    return new PeekLStream<>(this, action);
  }

  @Override
  public LStream<T> limit(long maxSize) {
    return new LimitLStream<>(this, maxSize);
  }

  @Override
  public LStream<T> skip(long nbToSkip) {
    requirePositive(nbToSkip);
    return new SkipLStream<>(this, nbToSkip);
  }

  protected static void requirePositive(long nbToSkip) throws IllegalArgumentException {
    if (nbToSkip < 0) {
      throw new IllegalArgumentException(
          "nbToSkip is %d but it should be positive".formatted(nbToSkip));
    }
  }

  @Override
  public LStream<T> takeWhile(Predicate<? super T> predicate) {
    return new takeWhileLStream<>(this, predicate);
  }

  @Override
  public LStream<T> takeWhilePrevious(Predicate<? super T> previousPredicate) {
    return new TakeWhilePreviousLStream<>(this, previousPredicate);
  }

  @Override
  public LStream<T> dropWhile(Predicate<? super T> predicate) {
    return new DropWhileLStream<>(this, predicate);
  }

  @Override
  public void forEach(Consumer<? super T> action) {
    while (hasNext()) {
      action.accept(next());
    }
  }

  @Override
  public Object[] toArray() {
    // TODO Implement so that there is no 1 copy to create the list and one copy to create the array
    return toList().toArray();
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {
    return toList().toArray(generator);
  }

  @Override
  public T reduce(T initialValue, BinaryOperator<T> accumulator) {
    T currentValue = initialValue;
    while (hasNext()) {
      currentValue = accumulator.apply(currentValue, next());
    }
    return currentValue;
  }

  @Override
  public <R> R reduce(R initialValue, BiFunction<R, T, R> accumulator) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = accumulator.apply(currentValue, next());
    }
    return currentValue;
  }

  @Override
  public Optional<T> reduce(BinaryOperator<T> accumulator) {

    if (!hasNext()) {
      return Optional.empty();
    }

    T accumulation = next();

    while (hasNext()) {
      accumulation = accumulator.apply(accumulation, next());
    }

    return Optional.ofNullable(accumulation);
  }

  @Override
  public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return collect(LCollector.of(supplier, accumulator));
  }

  @Override
  public <R> R collect(LCollector<? super T, ?, R> collector) {
    return collector.collect(this);
  }

  @Override
  public List<T> toList() {
    return collect(LCollectors.toUnmodifiableList());
  }

  @Override
  public Optional<T> min(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMinimum =
        (previousMin, nextValue) ->
            comparator.compare(previousMin, nextValue) > 0 ? nextValue : previousMin;

    return reduce(keepMinimum);
  }

  @Override
  public Optional<T> max(Comparator<? super T> comparator) {

    BinaryOperator<T> keepMaximum =
        (previousMax, nextValue) ->
            comparator.compare(previousMax, nextValue) < 0 ? nextValue : previousMax;

    return reduce(keepMaximum);
  }

  @Override
  public long count() {
    long nbElementsIterated = 0;
    while (hasNext()) {
      next();
      nbElementsIterated++;
    }
    return nbElementsIterated;
  }

  @Override
  public boolean anyMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (predicate.test(next())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean allMatch(Predicate<? super T> predicate) {
    while (hasNext()) {
      if (!predicate.test(next())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean noneMatch(Predicate<? super T> predicate) {
    return !anyMatch(predicate);
  }

  @Override
  public Optional<T> findFirst() {
    return hasNext() ? Optional.ofNullable(next()) : Optional.empty();
  }

  /**
   * @throws SeveralElementsException if there are several elements left in the stream
   */
  @Override
  public Optional<T> findOne() throws SeveralElementsException {
    Optional<T> first = findFirst();
    if (hasNext()) {
      throw new SeveralElementsException(
          "Call to \"findOne\" but there were several elements left in the stream");
    }
    return first;
  }

  @Override
  public Optional<T> findLast() {
    return reduce((first, second) -> second);
  }

  @Override
  public LStream<T> iterator() {
    return this;
  }

  public static <T> LStream.Builder<T> builder() {
    return new Builder<>();
  }

  public static <T> EmptyLStream<T> empty() {
    return new EmptyLStream<>();
  }

  public static <T> LStream<T> from(Iterable<T> iterable) {
    return from(iterable.iterator());
  }

  public static <T> LStream<T> from(Iterator<T> iterator) {
    return new SimpleLStream<>(iterator);
  }

  public static <T> LStream<T> from(List<T> iterationObjects) {
    if (iterationObjects instanceof RandomAccess) {
      return new ListRandomAccessLStream<>(iterationObjects);
    } else {
      return from((Iterable<T>) iterationObjects);
    }
  }

  @SafeVarargs
  public static <T> LStream<T> of(T... iterationObjects) {
    return new ArrayLStream<>(iterationObjects);
  }

  public static <T> LStream<T> iterate(
      final T initialValue, final UnaryOperator<T> computeNextValue) {
    return iterate(initialValue, value -> true, computeNextValue);
  }

  public static <T> LStream<T> iterate(
      T initialValue, Predicate<? super T> hasNext, UnaryOperator<T> next) {
    return new IterateLStream<>(initialValue, hasNext, next);
  }

  public static <T> LStream<T> generate(Supplier<? extends T> nextValueGenerator) {
    return generate(nextValueGenerator, value -> true);
  }

  public static <T> LStream<T> generate(
      Supplier<? extends T> nextValueGenerator, Predicate<? super T> hasNext) {
    return new GenerateLStream<>(nextValueGenerator, hasNext);
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
