package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Iterator;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappingLstream<T, R> extends LStream<R> {

  private final Iterator<T> previousIterator;
  private final Function<? super T, ? extends R> mapper;

  @Override
  public boolean hasNext() {
    return previousIterator.hasNext();
  }

  @Override
  public R next() {
    return mapper.apply(previousIterator.next());
  }
}
