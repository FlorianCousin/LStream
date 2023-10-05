package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Iterator;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TakeWhilePreviousIterator<T> extends LStream<T> {

  private final Iterator<T> baseIterator;
  private final Predicate<? super T> previousPredicate;

  private T previous;
  private boolean started = false;

  @Override
  public boolean hasNext() {
    return baseIterator.hasNext() && (!started || previousPredicate.test(previous));
  }

  @Override
  public T next() {
    started = true;
    return previous = baseIterator.next();
  }
}
