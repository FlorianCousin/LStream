package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleLStream<T> extends LStream<T> {

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
