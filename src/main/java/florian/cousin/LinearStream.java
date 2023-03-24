package florian.cousin;

import florian.cousin.collector.LinearCollector;
import florian.cousin.collector.SimpleCollector;
import florian.cousin.iterator.FilterIterator;
import florian.cousin.iterator.FlatMappingIterator;
import florian.cousin.iterator.MappingIterator;
import florian.cousin.iterator.SimpleIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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
