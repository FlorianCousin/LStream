package florian.cousin;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LinearStream<T> implements Iterator<T> {

  public static <T> LinearStream<T> from(Iterable<T> iterable) {
    return new SimpleIterator<>(iterable.iterator());
  }

  @SafeVarargs
  public static <T> LinearStream<T> of(T... iterationObjects) {
    return new SimpleIterator<>(List.of(iterationObjects).iterator());
  }

  public LinearStream<T> filter(Predicate<T> predicate) {
    return new FilterIterator<>(this, predicate);
  }

  public <R> LinearStream<R> map(Function<T, R> mapping) {
    return new MappingIterator(this, mapping);
  }
}
