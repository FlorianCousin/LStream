package florian.cousin;

import florian.cousin.collector.LinearCollector;
import florian.cousin.collector.SimpleCollector;
import florian.cousin.iterator.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public interface LinearStream<T> extends Iterator<T> {

  static <T> LinearStream<T> from(Iterable<T> iterable) {
    return new SimpleIterator<>(iterable.iterator());
  }

  @SafeVarargs
  static <T> LinearStream<T> of(T... iterationObjects) {
    return new SimpleIterator<>(List.of(iterationObjects).iterator());
  }

  static <T> LinearStream<T> empty() {
    return new SimpleIterator<>(Collections.emptyIterator());
  }

  default LinearStream<T> filter(Predicate<? super T> predicate) {
    return new FilterIterator<>(this, predicate);
  }

  default <R> LinearStream<R> map(Function<? super T, ? extends R> mapping) {
    return new MappingIterator<>(this, mapping);
  }

  default <R> LinearStream<R> flatMap(
      Function<? super T, ? extends LinearStream<? extends R>> mapper) {
    return new FlatMappingIterator<>(this, mapper);
  }

  default LinearStream<T> distinct() {
    return new DistinctIterator<>(this);
  }

  default LinearStream<T> sorted() {
    return sorted(null);
  }

  default LinearStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    return new SortedIterator<>(this, comparator);
  }

  default LinearStream<T> peek(Consumer<? super T> action) {
    return new PeekIterator<>(this, action);
  }

  default LinearStream<T> limit(long maxSize) {
    return new LimitIterator<>(this, maxSize);
  }

  default LinearStream<T> skip(long nbToSkip) {
    return new SkipIterator<>(this, nbToSkip);
  }

  default LinearStream<T> takeWhile(Predicate<? super T> predicate) {
    return new takeWhileIterator<>(this, predicate);
  }

  default LinearStream<T> dropWhile(Predicate<? super T> predicate) {
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

  default Optional<T> reduce(BinaryOperator<T> accumulator) {
    Optional<T> reducedOptional = Optional.empty();
    while (hasNext()) {
      T next = next();
      T reducedValue = reducedOptional.map(value -> accumulator.apply(value, next)).orElse(next);
      reducedOptional = Optional.ofNullable(reducedValue);
    }
    return reducedOptional;
  }

  default <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = aggregate.apply(currentValue, next());
    }
    return currentValue;
  }

  default <R> R collect(LinearCollector<T, ?, R> collector) {
    return collector.collect(this);
  }

  default List<T> toList() {
    return collect(new SimpleCollector<>(ArrayList::new, List::add));
  }
}
