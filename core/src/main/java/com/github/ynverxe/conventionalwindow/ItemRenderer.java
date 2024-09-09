package com.github.ynverxe.conventionalwindow;

import static com.github.ynverxe.conventionalwindow.util.ItemMathUtil.*;

import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ItemRenderer<V> implements RelativeItemContainer.Listener, StackedItemContainer.Listener {

  private final SimpleMenu<V, ?> menu;
  private final PlatformHandler<V> platformHandler;
  private final ItemProvider[] providersInUse;

  // index -> slot position
  private final Map<Integer, Integer> pageableItemIndexCache;
  private final LinkedList<Integer> freeSlots = new LinkedList<>();

  // pagination
  private int page = 0;

  public ItemRenderer(SimpleMenu<V, ?> menu, PlatformHandler<V> platformHandler, int capacity) {
    this.menu = menu;
    this.platformHandler = platformHandler;
    this.pageableItemIndexCache = new TreeMap<>(Integer::compareTo);
    this.providersInUse = new ItemProvider[capacity];
  }

  void init() {
    this.calculateFreeSlots();

    for (int i = 0; i < menu.capacity(); i++) {
      this.pageableItemIndexCache.put(i, -1);
    }

    this.clearProviders();
  }

  private void calculateFreeSlots() {
    this.freeSlots.clear();
    this.freeSlots.addAll(freeSlots(menu));
  }

  private boolean isInPage(int index, int page) {
    int itemsPerPage = itemsPerPage(menu);
    int start = startOfPage(itemsPerPage, page);
    int end = endOfPage(itemsPerPage, page);
    return index >= start || index < end;
  }

  private @NotNull ItemProvider placedItem(int slot) {
    return slot != -1 ? providersInUse[slot] : ItemProvider.AIR;
  }

  private void clearProviders() {
    Arrays.fill(this.providersInUse, ItemProvider.AIR);
  }

  private void calculateItemDistribution() {
    clearProviders();
    pageableItemIndexCache.clear();

    menu.staticItemContainer().forEach(this::insert);

    calculateFreeSlots();

    renderPageableItems(false);
  }

  private void renderPageableItems(boolean fillWithAirIfNeeded) {
    int itemsPerPage = itemsPerPage(menu);

    int start = startOfPage(itemsPerPage, page);
    int end = endOfPage(itemsPerPage, page);

    for (int index = start; index < end; index++) {
      StackedItemContainer stackedItemContainer = menu.pageableItemContainer();

      boolean invalidIndex = index >= stackedItemContainer.count();

      if (invalidIndex && !fillWithAirIfNeeded) {
        continue;
      }

      Integer slot = freeSlots.poll();

      if (slot == null) return;

      ItemProvider provider = invalidIndex ? ItemProvider.AIR : stackedItemContainer.get(index);
      insertPageableItem(slot, index, provider);
    }
  }

  private void insertPageableItem(int slot, int index, ItemProvider provider) {
    insert(slot, provider);
    pageableItemIndexCache.put(index, slot);
  }

  private void insert(int slot, ItemProvider provider) {
    providersInUse[slot] = provider;
    ItemStack itemStack = provider.get();
    if (itemStack == null) itemStack = ItemStack.AIR;
    menu.inventory().setItemStack(slot, itemStack);
  }

  @Override
  public void handleStaticItemInsert(int key, @NotNull ItemProvider itemProvider,
      @Nullable ItemProvider previous) {
    previous = previous != null ? previous : ItemProvider.AIR;

    synchronized (this) {
      if (previous == ItemProvider.AIR) {
        calculateItemDistribution();
        return;
      }

      insert(key, itemProvider);
    }
  }

  @Override
  public void handlePageableItemInsertion(int index, @NotNull ItemProvider provider) {
    synchronized (this) {
      int slot = pageableItemIndexCache.get(index);

      if (slot != -1) {
        insertPageableItem(slot, index, provider);
      } else {
        calculateItemDistribution();
      }
    }
  }

  @Override
  public void handlePageableItemsShift(int index, @NotNull ItemProvider provider) {
    synchronized (this) {
      if (!isInPage(index, page)) return; // out of the current page, don't need to be rendered

      StackedItemContainer container = menu.pageableItemContainer();

      if (container.getLast() == provider) { // no elements in front of this, no shift made
        Integer nextFreeSlot = freeSlots.poll();

        if (nextFreeSlot == null) return;

        insertPageableItem(nextFreeSlot, index, provider);
      } else {
        ItemProvider temp = provider;

        StackedItemContainer stackedItemContainer = menu.pageableItemContainer();

        // Shift and render
        while (isInPage(index, page) && !freeSlots.isEmpty() && index < stackedItemContainer.count()) {
          Integer nextFreeSlot = freeSlots.poll();

          if (nextFreeSlot == null) return;

          ItemProvider current = stackedItemContainer
              .get(index);

          insertPageableItem(nextFreeSlot, index, temp);
          temp = current;

          index++;
        }
      }
    }
  }

  public void pageChange(int newPage) {
    this.page = newPage;
    this.freeSlots.clear();
    this.freeSlots.addAll(this.pageableItemIndexCache.values());

    renderPageableItems(true);
  }

  public void updateItems() {
    synchronized (this) {
      int slot = 0;
      for (ItemProvider provider : this.providersInUse) {
        ItemStack itemStack = provider.get();
        menu.inventory().setItemStack(slot++, itemStack != null ? itemStack : ItemStack.AIR);
      }
    }
  }
}