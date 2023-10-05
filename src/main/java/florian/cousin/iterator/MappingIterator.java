package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Iterator;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappingIterator<T, R> implements LStream<R> {

  private final Iterator<T> previousIterator;
  private final Function<? super T, ? extends R> mapping;

  @Override
  public boolean hasNext() {
    return previousIterator.hasNext();
  }

  @Override
  public R next() {
    return mapping.apply(previousIterator.next());
  }
}
