package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SkipLStream<T> extends LStream<T> {

  private final LStream<T> baseIterator;
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
