package cousin.florian.utils;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedSupplier<Element> implements Supplier<Element> {

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
