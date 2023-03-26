package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateIterator<T> implements LinearStream<T> {

  private final Supplier<? extends T> nextValueGenerator;

  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public T next() {
    return nextValueGenerator.get();
  }
}
