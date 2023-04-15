package florian.cousin.iterator;

import florian.cousin.LinearStream;
import florian.cousin.collector.SimpleCollector;
import java.util.*;
import java.util.stream.Stream;
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

    SimpleCollector<T, PriorityQueue<T>> priorityQueueCollector =
        new SimpleCollector<>(() -> new PriorityQueue<>(comparator), PriorityQueue::add);

    PriorityQueue<T> baseIteratorValues = baseIterator.collect(priorityQueueCollector);

    // TODO Use LinearStream instead of Stream here when null values are allowed in takeWhile
    return Stream.generate(baseIteratorValues::poll).takeWhile(Objects::nonNull).iterator();
  }
}
