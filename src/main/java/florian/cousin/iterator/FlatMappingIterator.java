package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

public class FlatMappingIterator<T, R> extends LStream<R> {

  private final Iterator<T> baseIterator;
  private final Function<? super T, ? extends Iterator<? extends R>> mapper;
  private Iterator<? extends R> currentMappingIterator;

  public FlatMappingIterator(
      Iterator<T> baseIterator, Function<? super T, ? extends Iterator<? extends R>> mapper) {
    this.baseIterator = baseIterator;
    this.mapper = mapper;
    this.currentMappingIterator =
        baseIterator.hasNext() ? mapper.apply(baseIterator.next()) : Collections.emptyIterator();
  }

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
      currentMappingIterator = mapper.apply(baseIterator.next());
    }
  }
}
