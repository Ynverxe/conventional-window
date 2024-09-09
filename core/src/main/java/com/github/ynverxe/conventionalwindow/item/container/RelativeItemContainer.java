package com.github.ynverxe.conventionalwindow.item.container;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.item.AbstractItemContainer;
import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelativeItemContainer extends ConcurrentHashMap<Integer, ItemProvider>
    implements AbstractItemContainer {

  private final Menu<?, ?, ?> menu;
  private final Listener listener;
  private final int capacity;

  public RelativeItemContainer(@NotNull Menu<?, ?, ?> menu, @NotNull Listener listener, int capacity) {
    this.menu = Objects.requireNonNull(menu, "menu");
    this.listener = Objects.requireNonNull(listener, "listener");
    this.capacity = capacity;
  }

  @Override
  public ItemProvider put(@NotNull Integer key, @Nullable ItemProvider value) {
    if (key < 0 || key > maxFindableIndex())
      throw new IndexOutOfBoundsException("key out of bounds (" + key + ")");

    value = value != null ? value : ItemProvider.AIR;
    ItemProvider previous = super.put(key, value);
    listener.handleStaticItemInsert(key, value, previous);
    return previous;
  }

  @Override
  public boolean remove(Object key, Object value) {
    ItemProvider removed = super.remove(key);
    if (removed != null) {
      listener.handleStaticItemInsert((int) key, (ItemProvider) value, removed);
    }

    return removed != null;
  }

  @Override
  public @NotNull ItemProvider get(int index) {
    if (index < 0 || index > maxFindableIndex())
      throw new IndexOutOfBoundsException("key out of bounds (" + index + ")");

    ItemProvider found = super.get(index);
    return found != null ? found : ItemProvider.AIR;
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
        .filter(itemProvider -> itemProvider != ItemProvider.AIR)
        .count();
  }

  @Override
  public RelativeItemContainer fill(@NotNull SlotIterator iterator, @NotNull ItemProvider provider) {
    while (iterator.hasNext(menu.type())) {
      int next = iterator.next(menu.type());
      put(next, provider.fork());
    }

    return this;
  }

  public interface Listener {
    void handleStaticItemInsert(int key, @NotNull ItemProvider itemProvider, @Nullable ItemProvider previous);
  }
}
