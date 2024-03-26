package cousin.florian.utils;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

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
