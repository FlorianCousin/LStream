package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SkipLStream<T> extends LStream<T> {

  private final Iterator<T> baseIterator;
  private final long nbToSkip;

  private long nbSkipped = 0;

  @Override
  public boolean hasNext() {

    skipNeeded();

    return baseIterator.hasNext();
  }

  @Override
  public T next() {

    if (hasNext()) {
      return baseIterator.next();
    } else {
      throw new NoSuchElementException("no more elements");
    }
  }

  private void skipNeeded() {

    while (nbSkipped < nbToSkip && baseIterator.hasNext()) {
      baseIterator.next();
      nbSkipped++;
    }
  }
}
