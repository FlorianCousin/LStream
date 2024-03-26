package cousin.florian.utils;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Supplier;

@SuppressWarnings("java:S2160") // equals uses ListItr which uses get so equals is still correct
public class SuppliedAccessList<Element> extends AbstractList<Element> implements RandomAccess {

  private final CachedSupplier<List<Element>> elementsSupplier;
  private final int knownSize;

  public SuppliedAccessList(Supplier<List<Element>> listSupplier) {
    this.elementsSupplier = new CachedSupplier<>(listSupplier);
    this.knownSize = -1;
  }

  public SuppliedAccessList(Supplier<List<Element>> elementsSupplier, int knownSize) {
    this.elementsSupplier = new CachedSupplier<>(elementsSupplier);
    this.knownSize = knownSize;
  }

  @Override
  public Element get(int index) {
    return elementsSupplier.get().get(index);
  }

  @Override
  public int size() {
    return knownSize < 0 ? elementsSupplier.get().size() : knownSize;
  }
}
