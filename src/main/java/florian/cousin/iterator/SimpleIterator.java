package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleIterator<T> implements LStream<T> {

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
