package florian.cousin.utils;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Supplier;

@SuppressWarnings("java:S2160") // equals uses ListItr which uses get so equals is still correct
public class SuppliedAccessList<Element> extends AbstractList<Element> implements RandomAccess {

  private final CachedSupplier<List<Element>> elementsSupplier;

  public SuppliedAccessList(Supplier<List<Element>> listSupplier) {

    this.elementsSupplier = new CachedSupplier<>(listSupplier);
  }

  @Override
  public Element get(int index) {
    return elementsSupplier.get().get(index);
  }

  @Override
  public int size() {
    return elementsSupplier.get().size();
  }
}
