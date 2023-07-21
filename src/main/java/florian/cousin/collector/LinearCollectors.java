package florian.cousin.collector;

import java.util.*;
import java.util.function.Supplier;

public final class LinearCollectors {

  private LinearCollectors() {
    throw new IllegalStateException("This is a utility class.");
  }

  public static <T, C extends Collection<T>> LinearCollector<T, C, C> toCollection(
      Supplier<C> collectionFactory) {
    return LinearCollector.of(collectionFactory, Collection::add);
  }

  public static <T> LinearCollector<T, List<T>, List<T>> toList() {
    return LinearCollector.of(ArrayList::new, List::add);
  }

  public static <T> LinearCollector<T, List<T>, List<T>> toUnmodifiableList() {
    return LinearCollector.of(ArrayList::new, List::add, Collections::unmodifiableList);
  }

  public static <T> LinearCollector<T, Set<T>, Set<T>> toSet() {
    return LinearCollector.of(HashSet::new, Set::add);
  }

  public static <T> LinearCollector<T, Set<T>, Set<T>> toUnmodifiableSet() {
    return LinearCollector.of(HashSet::new, Set::add, Collections::unmodifiableSet);
  }

  public static LinearCollector<CharSequence, StringBuilder, String> joining() {
    return LinearCollector.of(StringBuilder::new, StringBuilder::append, StringBuilder::toString);
  }
}
