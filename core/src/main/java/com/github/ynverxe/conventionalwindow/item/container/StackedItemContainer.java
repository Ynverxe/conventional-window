package com.github.ynverxe.conventionalwindow.item.container;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackedItemContainer<I extends MenuItem<?, ?>> extends AbstractList<I> implements
    AbstractItemContainer<I> {

  private final Menu<I, ?, ?, ?> menu;
  private final List<I> itemProviders = new ArrayList<>();
  private final Listener listener;

  public StackedItemContainer(@NotNull Menu<I, ?, ?, ?> menu, @NotNull Listener listener) {
    this.menu = Objects.requireNonNull(menu, "menu");
    this.listener = Objects.requireNonNull(listener, "listener");
  }

  @Override
  public @Nullable I get(int index) {
    if (index < 0 || index >= count())
      throw new IndexOutOfBoundsException("index out of bounds (" + index + ")");

    return itemProviders.get(index);
  }

  @Override
  public int maxFindableIndex() {
    return count();
  }

  @Override
  public I set(int index, @Nullable I element) {
    I previous = itemProviders.set(index, element);
    listener.handlePageableItemInsertion(index, element);
    return previous;
  }

  @Contract("_, _ -> this")
  public StackedItemContainer<I> insert(int index, @Nullable I element) {
    set(index, element);
    return this;
  }

  @Override
  public void add(int index, @Nullable I element) {
    itemProviders.add(index, element);
    listener.handlePageableItemsShift(index, element);
  }

  @Contract("_ -> this")
  public StackedItemContainer<I> append(@Nullable I... providers) {
    this.addAll(Arrays.asList(providers));
    return this;
  }

  @Override
  public I remove(int index) {
    I previous = itemProviders.remove(index);
    if (previous != null) {
      listener.handlePageableItemInsertion(index, previous);
    }
    return previous;
  }

  @Override
  public int count() {
    return itemProviders.size();
  }

  @Override
  public int nonNullCount() {
    return (int) itemProviders.stream()
        .filter(Objects::nonNull)
        .count();
  }

  @Override
  public StackedItemContainer<I> fill(@NotNull SlotIterator iterator, @NotNull I provider) {
    while (iterator.hasNext(menu.type())) {
      int next = iterator.next(menu.type());

      int diff = next - size();
      while (diff > 0) {
        add(null);
        diff--;
      }

      add((I) provider.copy());
    }

    return this;
  }

  @Override
  public int size() {
    return count();
  }

  public interface Listener {
    void handlePageableItemInsertion(int index, @Nullable MenuItem<?, ?> provider);

    void handlePageableItemsShift(int index, @Nullable MenuItem<?, ?> provider);
  }
}