package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LimitIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;
  private final long maxSize;
  private long iteration;

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
