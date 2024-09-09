package com.github.ynverxe.conventionalwindow.util;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import java.util.ArrayList;
import java.util.List;

public final class ItemMathUtil {

  private ItemMathUtil() {}

  public static int startOfPage(int capacity, int page) {
    return capacity * page;
  }

  public static int endOfPage(int capacity, int page) {
    return startOfPage(capacity, page) + capacity;
  }

  public static List<Integer> freeSlots(Menu<?, ?, ?> menu) {
    List<Integer> free = new ArrayList<>();

    for (int slot = 0; slot < menu.capacity(); slot++) {
      if (isPageableSlot(menu, slot)) {
        free.add(slot);
      }
    }

    return free;
  }

  public static boolean isPageableSlot(Menu<?, ?, ?> menu, int slot) {
    return menu.staticItemContainer().get(slot) == ItemProvider.AIR;
  }

  public static int itemsPerPage(Menu<?, ?, ?> menu) {
    return menu.capacity() - menu.staticItemContainer().nonNullCount();
  }

  public static int pageCount(Menu<?, ?, ?> menu) {
    return (int) Math.ceil((double) menu.pageableItemContainer().count() / itemsPerPage(menu));
  }
}
