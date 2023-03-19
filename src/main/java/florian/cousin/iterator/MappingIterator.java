package florian.cousin.iterator;

import java.util.Iterator;
import java.util.function.Function;

import florian.cousin.LinearStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappingIterator<T, R> implements LinearStream<R> {

  private final Iterator<T> previousIterator;
  private final Function<T, R> mapping;

  @Override
  public boolean hasNext() {
    return previousIterator.hasNext();
  }

  @Override
  public R next() {
    return mapping.apply(previousIterator.next());
  }
}
