package cousin.florian.iterator;

import cousin.florian.LStream;
import cousin.florian.exception.SeveralElementsException;
import cousin.florian.utils.SuppliedAccessList;
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
    List<Element> values = new ArrayList<>(baseIterator.toList());
    values.sort(comparator);
    return values;
  }
}
