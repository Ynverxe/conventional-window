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

public class StackedItemContainer extends AbstractList<MenuItem<?>> implements
    AbstractItemContainer {

  private final Menu<?, ?, ?> menu;
  private final List<MenuItem<?>> menuItems = new ArrayList<>();
  private final Listener listener;

  public StackedItemContainer(@NotNull Menu<?, ?, ?> menu, @NotNull Listener listener) {
    this.menu = Objects.requireNonNull(menu, "menu");
    this.listener = Objects.requireNonNull(listener, "listener");
  }

  @Override
  public @Nullable MenuItem<?> get(int index) {
    if (index < 0 || index >= count())
      throw new IndexOutOfBoundsException("index out of bounds (" + index + ")");

    return menuItems.get(index);
  }

  @Override
  public int maxFindableIndex() {
    return count();
  }

  @Override
  public MenuItem<?> set(int index, @Nullable MenuItem<?> element) {
    MenuItem<?> previous = menuItems.set(index, element);
    listener.handlePageableItemInsertion(index, element);
    return previous;
  }

  @Contract("_, _ -> this")
  public StackedItemContainer insert(int index, @Nullable MenuItem<?> element) {
    set(index, element);
    return this;
  }

  @Override
  public void add(int index, @Nullable MenuItem<?> element) {
    menuItems.add(index, element);
    listener.handlePageableItemsShift(index, element);
  }

  @Contract("_ -> this")
  public StackedItemContainer append(@Nullable MenuItem<?>... providers) {
    this.addAll(Arrays.asList(providers));
    return this;
  }

  @Override
  public MenuItem<?> remove(int index) {
    MenuItem<?> previous = menuItems.remove(index);
    if (previous != null) {
      listener.handlePageableItemInsertion(index, previous);
    }
    return previous;
  }

  @Override
  public int count() {
    return menuItems.size();
  }

  @Override
  public int nonNullCount() {
    return (int) menuItems.stream()
        .filter(Objects::nonNull)
        .count();
  }

  @Override
  public StackedItemContainer fill(@NotNull SlotIterator iterator, @NotNull MenuItem<?> menuItem) {
    while (iterator.hasNext(menu.type())) {
      int next = iterator.next(menu.type());

      int diff = next - size();
      while (diff > 0) {
        add(null);
        diff--;
      }

      add(menuItem.copy());
    }

    return this;
  }

  @Override
  public int size() {
    return count();
  }

  public interface Listener {
    void handlePageableItemInsertion(int index, @Nullable MenuItem<?> menuItem);

    void handlePageableItemsShift(int index, @Nullable MenuItem<?> menuItem);
  }
}