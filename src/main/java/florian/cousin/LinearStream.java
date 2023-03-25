package florian.cousin;

import florian.cousin.collector.LinearCollector;
import florian.cousin.collector.SimpleCollector;
import florian.cousin.iterator.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public interface LinearStream<T> extends Iterator<T> {

  static <T> LinearStream<T> from(Iterable<T> iterable) {
    return new SimpleIterator<>(iterable.iterator());
  }

  @SafeVarargs
  static <T> LinearStream<T> of(T... iterationObjects) {
    return new SimpleIterator<>(List.of(iterationObjects).iterator());
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

  default <R> R collect(LinearCollector<T, ?, R> collector) {
    return collector.collect(this);
  }

  default List<T> toList() {
    return collect(new SimpleCollector<>(ArrayList::new, List::add));
  }

  default <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = aggregate.apply(currentValue, next());
    }
    return currentValue;
  }
}
