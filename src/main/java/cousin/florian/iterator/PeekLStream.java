package cousin.florian.iterator;

import cousin.florian.LStream;
import java.util.Iterator;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PeekLStream<T> extends LStream<T> {

  private final Iterator<T> baseIterator;
  private final Consumer<? super T> action;

  @Override
  public boolean hasNext() {
    return baseIterator.hasNext();
  }

  @Override
  public T next() {
    T next = baseIterator.next();
    action.accept(next);
    return next;
  }
}
