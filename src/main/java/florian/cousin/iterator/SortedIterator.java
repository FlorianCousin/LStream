package florian.cousin.iterator;

import florian.cousin.LStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class SortedIterator<T> implements LStream<T> {

  private final LStream<T> baseIterator;
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
    List<T> values = baseIterator.toList();
    values.sort(comparator);
    return values.iterator();
  }
}
