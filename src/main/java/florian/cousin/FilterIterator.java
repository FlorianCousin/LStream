package florian.cousin;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public class FilterIterator<T> extends LinearStream<T> implements Iterator<T> {

  private final Predicate<T> predicate;
  private @Nullable T next;

  public FilterIterator(Iterator<T> iterator, Predicate<T> predicate) {
    super(iterator);
    this.predicate = predicate;
    this.next = null;
  }

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
