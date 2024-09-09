package com.github.ynverxe.conventionalwindow.slot;

import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class FixedSlotIterator implements SlotIterator {

  private final int delta;
  private int times;
  private int current;

  public FixedSlotIterator(int from, int times, int delta) {
    this.times = times;
    this.current = from - delta;
    this.delta = delta;
  }

  @Override
  public boolean hasNext(@NotNull InventoryType type) {
    return times - 1 >= 0 && current + delta < type.getSize();
  }

  @Override
  public int next(@NotNull InventoryType type) {
    if (!hasNext(type)) {
      throw new IllegalStateException();
    }

    times--;
    return current += delta;
  }
}
