package florian.cousin.iterator;

import florian.cousin.LStream;
import florian.cousin.LStreamApi;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlatMappingIterator<T, R> extends LStream<R> {

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
      currentMappingIterator = mapper.apply(baseIterator.next()).iterator();
    }
  }
}
