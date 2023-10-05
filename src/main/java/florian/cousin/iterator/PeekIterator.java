package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PeekIterator<T> extends LStream<T> {

  private final LStream<T> baseIterator;
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
