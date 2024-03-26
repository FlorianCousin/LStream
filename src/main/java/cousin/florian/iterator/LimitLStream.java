package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LimitLStream<T> extends LStream<T> {

  private final Iterator<T> baseIterator;
  private final long maxSize;
  private long iteration = 0;

  @Override
  public boolean hasNext() {
    return baseIterator.hasNext() && iteration < maxSize;
  }

  @Override
  public T next() {

    if (hasNext()) {
      iteration++;
    } else {
      throw new NoSuchElementException("iteration limit has been reached");
    }

    return baseIterator.next();
  }
}
