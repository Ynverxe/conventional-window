package com.github.ynverxe.conventionalwindow.item.container;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelativeItemContainer<I extends MenuItem<?, ?>> extends ConcurrentHashMap<Integer, I>
    implements AbstractItemContainer<I> {

  private final Menu<I, ?, ?, ?> menu;
  private final Listener listener;
  private final int capacity;

  public RelativeItemContainer(@NotNull Menu<I, ?, ?, ?> menu, @NotNull Listener listener, int capacity) {
    this.menu = Objects.requireNonNull(menu, "menu");
    this.listener = Objects.requireNonNull(listener, "listener");
    this.capacity = capacity;
  }

  @Override
  public @Nullable I put(@NotNull Integer key, @Nullable I value) {
    if (key < 0 || key > maxFindableIndex())
      throw new IndexOutOfBoundsException("key out of bounds (" + key + ")");

    if (value == null) return remove(key);

    I previous = super.put(key, value);
    listener.handleStaticItemInsert(key, value, previous);
    return previous;
  }

  @Override
  public boolean remove(Object key, Object value) {
    I removed = super.remove(key);
    if (removed != null) {
      listener.handleStaticItemInsert((int) key, (I) value, removed);
    }

    return removed != null;
  }

  @Override
  public @Nullable I get(int index) {
    if (index < 0 || index > maxFindableIndex())
      throw new IndexOutOfBoundsException("key out of bounds (" + index + ")");

    return super.get(index);
  }

  @Override
  public int maxFindableIndex() {
    return capacity - 1;
  }

  @Override
  public int count() {
    return size();
  }

  public int capacity() {
    return capacity;
  }

  @Override
  public int nonNullCount() {
    return (int) values()
        .stream()
        .filter(Objects::nonNull)
        .count();
  }

  @Override
  public RelativeItemContainer<I> fill(@NotNull SlotIterator iterator, @NotNull I provider) {
    while (iterator.hasNext(menu.type())) {
      int next = iterator.next(menu.type());
      put(next, (I) provider.copy());
    }

    return this;
  }

  @Internal
  public interface Listener {
    void handleStaticItemInsert(int key, @Nullable MenuItem<?, ?> itemProvider, @Nullable MenuItem<?, ?> previous);
  }
}
