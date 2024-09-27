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

/**
 * This class is responsible for distributing and render the items.
 */
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

  /**
   * Checks if the index is bewteen the page's range.
   *
   * @param index The index to check
   * @param page The page
   * @return index >= minPageIndex && index < maxPageIndex
   */
  private boolean isInPage(int index, int page) {
    int itemsPerPage = itemsPerPage(menu);
    int start = startOfPage(itemsPerPage, page);
    int end = endOfPage(itemsPerPage, page);
    return index >= start && index < end;
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

    // Insert the static items
    menu.staticItemContainer().forEach(this::insert);

    // Now calculate the free slots (air)
    calculateFreeSlots();

    renderPageableItems(false);
  }

  /**
   * This method inserts a collection of pageable items in all free
   * slots.
   *
   * @param fillWithAirIfNeeded If true, the remaining free slots that cannot be consumed due that
   *                            all pageable items were rendered will be filled with {@link AirMenuItem#INSTANCE}
   */
  private void renderPageableItems(boolean fillWithAirIfNeeded) {
    int itemsPerPage = itemsPerPage(menu);

    int start = startOfPage(itemsPerPage, page);
    int end = endOfPage(itemsPerPage, page);

    for (int index = start; index < end; index++) {
      StackedItemContainer<?> stackedItemContainer = menu.pageableItemContainer();

      // We reached the end of the pageable items list. There are no more items to render.
      boolean invalidIndex = index >= stackedItemContainer.count();

      // When there are no more items to render, fill with air if possible
      if (invalidIndex && !fillWithAirIfNeeded) {
        continue;
      }

      // All free slots were consumed
      Integer slot = freeSlots.poll();

      if (slot == null) return;

      MenuItem<?, ?> menuItem = invalidIndex ? AirMenuItem.INSTANCE : stackedItemContainer.get(index);
      insertPageableItem(slot, index, menuItem);
    }
  }

  private void insertPageableItem(int slot, int index, MenuItem<?, ?> menuItem) {
    insert(slot, menuItem);
    pageableItemIndexCache.put(index, slot);
  }

  private void insert(int slot, @Nullable MenuItem<?, ?> menuItem) {
    providersInUse[slot] = menuItem;
  }

  @Override
  public void handleStaticItemInsert(int key, @Nullable MenuItem<?, ?> menuItem,
      @Nullable MenuItem<?, ?> previous) {
    synchronized (this) {
      // If the previous static item is null means that
      // can be a pageable item at that position, to avoid
      // overwriting it, we calculate the distribution again to push all subsequent
      // items forward.
      if (previous == null) {
        calculateItemDistribution();
        return;
      }

      insert(key, menuItem);
    }
  }

  @Override
  public void handlePageableItemInsertion(int index, @Nullable MenuItem<?, ?> menuItem) {
    synchronized (this) {
      int slot = pageableItemIndexCache.getOrDefault(index, -1);

      if (slot != -1) {
        insertPageableItem(slot, index, menuItem);
      } else {
        calculateItemDistribution();
      }
    }
  }

  @Override
  public void handlePageableItemsShift(int index, @Nullable MenuItem<?, ?> menuItem) {
    synchronized (this) {
      if (!isInPage(index, page)) return; // out of the current page, don't need to be rendered

      StackedItemContainer<?> container = menu.pageableItemContainer();

      if (container.getLast() == menuItem) { // no elements in front of this, no shift made
        Integer nextFreeSlot = freeSlots.poll();

        if (nextFreeSlot == null) return;

        insertPageableItem(nextFreeSlot, index, menuItem);
      } else {
        MenuItem<?, ?> temp = menuItem;

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

  /**
   * Handles the page change and renders the new pageable items.
   *
   * @param newPage The page to render
   */
  public void pageChange(int newPage) {
    this.page = newPage;

    // Use the previous pageable items' slots instead of calling freeSlots method
    // Clear to avoid repeated elements
    this.freeSlots.clear();
    this.freeSlots.addAll(this.pageableItemIndexCache.values());

    renderPageableItems(true);
  }

  /**
   * Tick items
   * @param context The ItemContext used to tick all rendered items
   */
  public void updateItems(@NotNull ItemContext context) {
    synchronized (this) {
      int slot = 0;
      for (MenuItem<?, ?> menuItem : this.providersInUse) {
        ItemStack itemStack = menuItem.get(context);
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