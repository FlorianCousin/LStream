package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.NoSuchElementException;

public class EmptyLStream<T> extends LStream<T> {

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public T next() {
    throw new NoSuchElementException("The iterator is empty.");
  }
}
