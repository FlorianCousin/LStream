package florian.cousin.iterator;

import florian.cousin.LStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcatLStream<T> extends LStream<T> {

  private final LStream<? extends T> first;
  private final LStream<? extends T> second;

  @Override
  public boolean hasNext() {
    return first.hasNext() || second.hasNext();
  }

  @Override
  public T next() {
    return first.hasNext() ? first.next() : second.next();
  }
}
