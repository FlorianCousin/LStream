package florian.cousin.iterator;

import florian.cousin.LinearStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class FilterIterator<T> implements LinearStream<T> {

  private final Iterator<T> iterator;
  private final Predicate<? super T> predicate;
  private @Nullable T next;

  @Override
  public boolean hasNext() {

    filterToNext();

    return next != null;
  }

  @Override
  public T next() {

    if (hasNext()) {
      T filteredNext = this.next;
      this.next = null;
      return filteredNext;
    } else {
      throw new NoSuchElementException("No more elements in filter iterator.");
    }
  }

  private void filterToNext() {
    while (iterator.hasNext() && next == null) {
      T currentNext = iterator.next();
      if (predicate.test(currentNext)) {
        next = currentNext;
      }
    }
  }
}
