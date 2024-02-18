package florian.cousin.iterator;

import florian.cousin.LStream;
import florian.cousin.collector.LCollectors;
import florian.cousin.exception.SeveralElementsException;
import java.util.*;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class SortedLStream<T> extends ListRandomAccessLStream<T> {

  private final LStream<T> baseIterator;

  public SortedLStream(LStream<T> baseIterator, @Nullable Comparator<? super T> comparator) {
    super(new SortedList<>(baseIterator, comparator));
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

  @RequiredArgsConstructor
  private static class CachedSupplier<Element> implements Supplier<Element> {

    private boolean cacheIsInitialised = false;
    private Element cachedValue;
    private final Supplier<Element> supplierWithoutCache;

    @Override
    public Element get() {

      if (cacheIsInitialised) {
        return cachedValue;
      }

      cachedValue = supplierWithoutCache.get();
      cacheIsInitialised = true;
      return cachedValue;
    }
  }

  @RequiredArgsConstructor
  @SuppressWarnings("java:S2160") // Only values are important in comparison of two lists
  private static class SortedList<Element> extends AbstractList<Element> implements RandomAccess {

    private final CachedSupplier<List<Element>> elementsSupplier;

    public SortedList(
        LStream<Element> baseIterator, @Nullable Comparator<? super Element> comparator) {

      this.elementsSupplier =
          new CachedSupplier<>(() -> supplySortedList(baseIterator, comparator));
    }

    @Override
    public Element get(int index) {
      return elementsSupplier.get().get(index);
    }

    @Override
    public int size() {
      return elementsSupplier.get().size();
    }

    private List<Element> supplySortedList(
        LStream<Element> baseIterator, @Nullable Comparator<? super Element> comparator) {
      List<Element> values = baseIterator.collect(LCollectors.toList());
      values.sort(comparator);
      return values;
    }
  }
}
