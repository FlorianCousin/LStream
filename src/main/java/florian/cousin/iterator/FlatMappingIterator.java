package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

public class FlatMappingIterator<T, R> implements LinearStream<R> {

  private final Iterator<T> baseIterator;
  private final Function<? super T, ? extends Iterator<? extends R>> mapping;
  private Iterator<? extends R> currentMappingIterator;

  public FlatMappingIterator(
      Iterator<T> baseIterator, Function<? super T, ? extends Iterator<? extends R>> mapping) {
    this.baseIterator = baseIterator;
    this.mapping = mapping;
    this.currentMappingIterator =
        baseIterator.hasNext() ? mapping.apply(baseIterator.next()) : Collections.emptyIterator();
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
      currentMappingIterator = mapping.apply(baseIterator.next());
    }
  }
}
