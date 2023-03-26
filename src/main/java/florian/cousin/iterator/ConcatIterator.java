package florian.cousin.iterator;

import florian.cousin.LinearStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcatIterator<T> implements LinearStream<T> {

  private final LinearStream<? extends T> first;
  private final LinearStream<? extends T> second;

  @Override
  public boolean hasNext() {
    return first.hasNext() || second.hasNext();
  }

  @Override
  public T next() {
    return first.hasNext() ? first.next() : second.next();
  }
}
