package florian.cousin.iterator;

import java.util.Iterator;

import florian.cousin.LinearStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleIterator<T> implements LinearStream<T> {

  private final Iterator<T> iterator;

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return iterator.next();
  }
}
