package com.github.ynverxe.conventionalwindow.slot;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class ComposedSlotIterator implements SlotIterator {

  private final Queue<SlotIterator> iterators;

  public ComposedSlotIterator(SlotIterator @NotNull ... iterators) {
    this.iterators = new LinkedList<>(Arrays.asList(iterators));
  }

  @Override
  public boolean hasNext(@NotNull InventoryType type) {
    SlotIterator current = iterators.element();
    while (!current.hasNext(type)) {
      iterators.poll();
      current = iterators.peek();

      if (current == null) {
        return false;
      }
    }

    return true;
  }

  @Override
  public int next(@NotNull InventoryType type) {
    return iterators.element().next(type);
  }
}
