package florian.cousin.iterator;

import florian.cousin.LStream;
import florian.cousin.collector.LCollectors;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class SortedLStream<T> extends LStream<T> {

  // TODO extend ListRandomAccess ?
  //  given a list is created anyway

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

  @Override
  public long count() {
    return baseIterator.count();
  }

  @Override
  public Optional<T> findOne() throws SeveralElementsException {
    return baseIterator.findOne();
  }

  private Iterator<T> createSortedIterator() {
    List<T> values = baseIterator.collect(LCollectors.toList());
    values.sort(comparator);
    return values.iterator();
  }

  // TODO Implement things like findOne or count
}
