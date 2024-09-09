package com.github.ynverxe.conventionalwindow.item.container;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.item.AbstractItemContainer;
import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackedItemContainer extends AbstractList<ItemProvider> implements
    AbstractItemContainer {

  private final Menu<?, ?, ?> menu;
  private final List<ItemProvider> itemProviders = new ArrayList<>();
  private final Listener listener;

  public StackedItemContainer(@NotNull Menu<?, ?, ?> menu, @NotNull Listener listener) {
    this.menu = menu;
    this.listener = Objects.requireNonNull(listener, "listener");
  }

  @Override
  public @NotNull ItemProvider get(int index) {
    if (index < 0 || index >= count())
      throw new IndexOutOfBoundsException("index out of bounds (" + index + ")");

    return itemProviders.get(index);
  }

  @Override
  public int maxFindableIndex() {
    return count();
  }

  @Override
  public ItemProvider set(int index, @Nullable ItemProvider element) {
    element = element != null ? element : ItemProvider.AIR;
    ItemProvider previous = itemProviders.set(index, element);
    listener.handlePageableItemInsertion(index, element);
    return previous;
  }

  @Contract("_, _ -> this")
  public StackedItemContainer insert(int index, @Nullable ItemProvider element) {
    set(index, element);
    return this;
  }

  @Override
  public void add(int index, ItemProvider element) {
    element = element != null ? element : ItemProvider.AIR;
    itemProviders.add(index, element);
    listener.handlePageableItemsShift(index, element);
  }

  @Contract("_ -> this")
  public StackedItemContainer append(@Nullable ItemProvider... providers) {
    this.addAll(Arrays.asList(providers));
    return this;
  }

  @Override
  public ItemProvider remove(int index) {
    ItemProvider previous = itemProviders.remove(index);
    if (previous != null && previous != ItemProvider.AIR) {
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
        .filter(itemProvider -> itemProvider != ItemProvider.AIR)
        .count();
  }

  @Override
  public StackedItemContainer fill(@NotNull SlotIterator iterator, @NotNull ItemProvider provider) {
    while (iterator.hasNext(menu.type())) {
      int next = iterator.next(menu.type());

      int diff = next - size();
      while (diff > 0) {
        add(ItemProvider.AIR);
        diff--;
      }

      add(provider.fork());
    }

    return this;
  }

  @Override
  public int size() {
    return count();
  }

  public interface Listener {
    void handlePageableItemInsertion(int index, @NotNull ItemProvider provider);

    void handlePageableItemsShift(int index, @NotNull ItemProvider provider);
  }
}