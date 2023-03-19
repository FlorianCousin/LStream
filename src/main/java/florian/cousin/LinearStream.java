package florian.cousin;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinearStream<T> {

  protected final Iterator<T> iterator;

  public static <T> LinearStream<T> from(Iterable<T> iterable) {
    return new LinearStream(iterable.iterator());
  }

  public static <T> LinearStream<T> of(T... iterationObjects) {
    return new LinearStream(List.of(iterationObjects).iterator());
  }

  public LinearStream<T> filter(Predicate<T> predicate) {
    return new FilterIterator(iterator, predicate);
  }
}
