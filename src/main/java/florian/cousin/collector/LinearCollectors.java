package florian.cousin.collector;

import java.util.Collection;
import java.util.function.Supplier;

public final class LinearCollectors {

  private LinearCollectors() {
    throw new IllegalStateException("This is a utility class.");
  }

  public static <T, C extends Collection<T>> LinearCollector<T, C, C> toCollection(
      Supplier<C> collectionFactory) {
    return LinearCollector.of(collectionFactory, Collection::add);
  }
}
