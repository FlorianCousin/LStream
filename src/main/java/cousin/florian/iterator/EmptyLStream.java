package cousin.florian.iterator;

import cousin.florian.LStream;
import cousin.florian.exception.SeveralElementsException;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.Nullable;

public class EmptyLStream<T> extends LStream<T> {

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public T next() {
    throw new NoSuchElementException("The iterator is empty.");
  }

  @Override
  public EmptyLStream<T> filter(Predicate<? super T> predicate) {
    return this;
  }

  @Override
  public EmptyLStream<T> distinct() {
    return this;
  }

  @Override
  public EmptyLStream<T> sorted() {
    return this;
  }

  @Override
  public EmptyLStream<T> sorted(@Nullable Comparator<? super T> comparator) {
    return this;
  }

  @Override
  public EmptyLStream<T> peek(Consumer<? super T> action) {
    return this;
  }

  @Override
  public EmptyLStream<T> limit(long maxSize) {
    return this;
  }

  @Override
  public EmptyLStream<T> skip(long nbToSkip) {
    requirePositive(nbToSkip);
    return this;
  }

  @Override
  public EmptyLStream<T> takeWhile(Predicate<? super T> predicate) {
    return this;
  }

  @Override
  public EmptyLStream<T> takeWhilePrevious(Predicate<? super T> previousPredicate) {
    return this;
  }

  @Override
  public EmptyLStream<T> dropWhile(Predicate<? super T> predicate) {
    return this;
  }

  @Override
  public void forEach(Consumer<? super T> action) {
    // action is to be applied on each element but there is no element in an empty lstream
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {
    return generator.apply(0);
  }

  @Override
  public T reduce(T initialValue, BinaryOperator<T> accumulator) {
    return initialValue;
  }

  @Override
  public <R> R reduce(R initialValue, BiFunction<R, T, R> accumulator) {
    return initialValue;
  }

  @Override
  public Optional<T> reduce(BinaryOperator<T> accumulator) {
    return Optional.empty();
  }

  @Override
  public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
    return supplier.get();
  }

  @Override
  public List<T> toList() {
    return Collections.emptyList();
  }

  @Override
  public Optional<T> min(Comparator<? super T> comparator) {
    return Optional.empty();
  }

  @Override
  public Optional<T> max(Comparator<? super T> comparator) {
    return Optional.empty();
  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public boolean anyMatch(Predicate<? super T> predicate) {
    return false;
  }

  @Override
  public boolean allMatch(Predicate<? super T> predicate) {
    return true;
  }

  @Override
  public boolean noneMatch(Predicate<? super T> predicate) {
    return true;
  }

  @Override
  public Optional<T> findFirst() {
    return Optional.empty();
  }

  @Override
  public Optional<T> findOne() throws SeveralElementsException {
    return Optional.empty();
  }

  @Override
  public Optional<T> findLast() {
    return Optional.empty();
  }

  // TODO Same implementation as parent. Is it really useful ?
  @Override
  public EmptyLStream<T> iterator() {
    return this;
  }
}
