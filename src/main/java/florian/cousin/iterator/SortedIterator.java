package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class SortedIterator<T> implements LinearStream<T> {

  private final LinearStream<T> baseIterator;
  private final @Nullable Comparator<? super T> comparator;

  private @Nullable Iterator<T> sortedValues;

  @Override
  public boolean hasNext() {
    return baseIterator.hasNext() || (sortedValues != null && sortedValues.hasNext());
  }

  @Override
  public T next() {

    if (sortedValues == null) {
      sortedValues = createSortedIterator();
    }

    return sortedValues.next();
  }

  private Iterator<T> createSortedIterator() {
    List<T> values = baseIterator.collect(ArrayList::new, List::add);
    values.sort(comparator);
    return values.iterator();
  }
}
