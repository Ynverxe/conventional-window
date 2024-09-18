package com.github.ynverxe.conventionalwindow;

import static com.github.ynverxe.conventionalwindow.util.ItemMathUtil.*;

import com.github.ynverxe.conventionalwindow.item.AirMenuItem;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.util.ItemMathUtil;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ItemRenderer<I extends MenuItem<?, ?>> implements RelativeItemContainer.Listener, StackedItemContainer.Listener, RenderView<I> {

  private final SimpleMenu<?, ?, ?, ?> menu;
  private final MenuItem<?, ?>[] providersInUse;

  // index -> slot position
  private final Map<Integer, Integer> pageableItemIndexCache;
  private final LinkedList<Integer> freeSlots = new LinkedList<>();

  // pagination
  private int page = 0;

  public ItemRenderer(SimpleMenu<?, ?, ?, ?> menu, int capacity) {
    this.menu = menu;
    this.pageableItemIndexCache = new TreeMap<>(Integer::compareTo);
    this.providersInUse = new MenuItem[capacity];
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

  private @NotNull MenuItem<?, ?> placedItem(int slot) {
    return slot != -1 ? providersInUse[slot] : AirMenuItem.INSTANCE;
  }

  private void clearProviders() {
    Arrays.fill(this.providersInUse, AirMenuItem.INSTANCE);
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
      StackedItemContainer<?> stackedItemContainer = menu.pageableItemContainer();

      boolean invalidIndex = index >= stackedItemContainer.count();

      if (invalidIndex && !fillWithAirIfNeeded) {
        continue;
      }

      Integer slot = freeSlots.poll();

      if (slot == null) return;

      MenuItem<?, ?> provider = invalidIndex ? AirMenuItem.INSTANCE : stackedItemContainer.get(index);
      insertPageableItem(slot, index, provider);
    }
  }

  private void insertPageableItem(int slot, int index, MenuItem<?, ?> provider) {
    insert(slot, provider);
    pageableItemIndexCache.put(index, slot);
  }

  private void insert(int slot, @Nullable MenuItem<?, ?> provider) {
    providersInUse[slot] = provider;
  }

  @Override
  public void handleStaticItemInsert(int key, @Nullable MenuItem<?, ?> menuItem,
      @Nullable MenuItem<?, ?> previous) {
    synchronized (this) {
      if (previous == null) {
        calculateItemDistribution();
        return;
      }

      insert(key, menuItem);
    }
  }

  @Override
  public void handlePageableItemInsertion(int index, @Nullable MenuItem<?, ?> provider) {
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
  public void handlePageableItemsShift(int index, @Nullable MenuItem<?, ?> provider) {
    synchronized (this) {
      if (!isInPage(index, page)) return; // out of the current page, don't need to be rendered

      StackedItemContainer<?> container = menu.pageableItemContainer();

      if (container.getLast() == provider) { // no elements in front of this, no shift made
        Integer nextFreeSlot = freeSlots.poll();

        if (nextFreeSlot == null) return;

        insertPageableItem(nextFreeSlot, index, provider);
      } else {
        MenuItem<?, ?> temp = provider;

        StackedItemContainer<?> stackedItemContainer = menu.pageableItemContainer();

        // Shift and render
        while (isInPage(index, page) && !freeSlots.isEmpty() && index < stackedItemContainer.count()) {
          Integer nextFreeSlot = freeSlots.poll();

          if (nextFreeSlot == null) return;

          MenuItem<?, ?> current = stackedItemContainer
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

  public void updateItems(@NotNull ItemContext context) {
    synchronized (this) {
      int slot = 0;
      for (MenuItem<?, ?> provider : this.providersInUse) {
        ItemStack itemStack = provider.get(context);
        menu.inventory().setItemStack(slot++, itemStack != null ? itemStack : ItemStack.AIR);
      }
    }
  }

  @Override
  public @NotNull LinkedHashMap<Integer, I> asMap() {
    LinkedHashMap<Integer, I> map = new LinkedHashMap<>();
    for (int slot = 0; slot < providersInUse.length; slot++) {
      I item = getItem(slot);
      if (item != null) {
        map.put(slot, item);
      }
    }
    return map;
  }

  @Override
  public @Nullable I getItem(int slot) {
    MenuItem<?, ?> item = providersInUse[slot];
    ItemMathUtil.checkOutOfInventory(slot, providersInUse.length);

    return item == AirMenuItem.INSTANCE ? null : (I) item;
  }

  @Override
  public int length() {
    return providersInUse.length;
  }
}