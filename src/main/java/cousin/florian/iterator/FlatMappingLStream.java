package cousin.florian.iterator;

import cousin.florian.LStream;
import cousin.florian.LStreamApi;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlatMappingLStream<T, R> extends LStream<R> {

  private final Iterator<T> baseIterator;
  private final Function<? super T, ? extends LStreamApi<? extends R>> mapper;
  private Iterator<? extends R> currentMappingIterator = Collections.emptyIterator();

  @Override
  public boolean hasNext() {

    dropBaseIteratorUntilValue();

    return currentMappingIterator.hasNext();
  }

  @Override
  public R next() {

    dropBaseIteratorUntilValue();

    return currentMappingIterator.next();
  }

  private void dropBaseIteratorUntilValue() {
    while (!currentMappingIterator.hasNext() && baseIterator.hasNext()) {
      currentMappingIterator =
          Optional.ofNullable(mapper.apply(baseIterator.next()))
              .map(LStreamApi::iterator)
              .orElse(Collections.emptyIterator());
    }
  }
}
