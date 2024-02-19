package florian.cousin.iterator;

import florian.cousin.LStream;
import florian.cousin.collector.LCollectors;
import florian.cousin.exception.SeveralElementsException;
import florian.cousin.utils.SuppliedAccessList;
import java.util.*;
import org.jetbrains.annotations.Nullable;

public class SortedLStream<T> extends ListRandomAccessLStream<T> {

  private final LStream<T> baseIterator;

  public SortedLStream(LStream<T> baseIterator, @Nullable Comparator<? super T> comparator) {
    super(new SuppliedAccessList<>(() -> supplySortedList(baseIterator, comparator)));
    this.baseIterator = baseIterator;
  }

  @Override
  public long count() {
    return baseIterator.count();
  }

  @Override
  public Optional<T> findOne() throws SeveralElementsException {
    return baseIterator.findOne();
  }

  private static <Element> List<Element> supplySortedList(
      LStream<Element> baseIterator, @Nullable Comparator<? super Element> comparator) {
    List<Element> values = baseIterator.collect(LCollectors.toList());
    values.sort(comparator);
    return values;
  }
}
