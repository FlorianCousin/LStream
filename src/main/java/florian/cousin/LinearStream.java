package florian.cousin;

import java.util.ArrayList;
import java.util.Collections;
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

  default LinearStream<T> filter(Predicate<T> predicate) {
    return new FilterIterator<>(this, predicate);
  }

  default <R> LinearStream<R> map(Function<T, R> mapping) {
    return new MappingIterator<>(this, mapping);
  }

  default List<T> toList() {
    List<T> collectingList = new ArrayList<>();
    forEachRemaining(collectingList::add);
    return Collections.unmodifiableList(collectingList);
  }

  default <R> R reduce(R initialValue, BiFunction<R, T, R> aggregate) {
    R currentValue = initialValue;
    while (hasNext()) {
      currentValue = aggregate.apply(currentValue, next());
    }
    return currentValue;
  }
}
