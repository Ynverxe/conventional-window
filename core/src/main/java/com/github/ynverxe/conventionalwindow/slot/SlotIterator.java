package com.github.ynverxe.conventionalwindow.slot;

import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public interface SlotIterator {

  /**
   * @param from The slot to start (inclusive)
   * @param times The times the iterator will be executed.
   * @return a new SlotIterator that calculates slots horizontally.
   */
  static @NotNull SlotIterator horizontally(int from, int times) {
    return new FixedSlotIterator(from, times, 1);
  }

  /**
   * @param from The slot to start (inclusive)
   * @param times The times the iterator will be executed.
   * @param columns The columns of the inventory, this is used as delta.
   * @return a new SlotIterator that calculates slots vertically.
   */
  static @NotNull SlotIterator vertically(int from, int times, int columns) {
    return new FixedSlotIterator(from, times, columns);
  }

  /**
   * @param from The slot to start (inclusive)
   * @param times The times the iterator will be executed.
   * @return a new SlotIterator that calculates slots vertically for 3x3 inventories, except {@link
   *     InventoryType#CRAFTING}.
   */
  static @NotNull SlotIterator vertically3x3(int from, int times) {
    return vertically(from, times, 3);
  }

  /**
   * @param from The slot to start (inclusive)
   * @param times The times the iterator will be executed.
   * @return a new SlotIterator that calculates slots horizontally for inventories with nine
   *     columns.
   */
  static @NotNull SlotIterator vertically9Columns(int from, int times) {
    return new FixedSlotIterator(from, times, 9);
  }

  /**
   * @return a new SlotIterator that delegates all slot calculation logic to other iterators.
   */
  static @NotNull SlotIterator ofIterators(SlotIterator @NotNull ... iterators) {
    return new ComposedSlotIterator(iterators);
  }

  /**
   * @param rows The row count of the inventory
   * @param columns The column count of the inventory
   * @return a new SlotIterator that calculates the borders of an inventory with the provided rows
   *     and columns.
   */
  static @NotNull SlotIterator borders2D(int rows, int columns) {
    return ofIterators(
        horizontally(0, columns),
        horizontally((rows - 1) * columns, columns),
        vertically(columns, rows - 2, columns),
        vertically(((columns - 1) * 2) + 1, rows - 2, columns));
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_1_ROW}.
   */
  static @NotNull SlotIterator oneRowChestBorders() {
    return borders2D(1, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_2_ROW}.
   */
  static @NotNull SlotIterator twoRowsChestBorders() {
    return borders2D(2, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_3_ROW}.
   */
  static @NotNull SlotIterator threeRowsChestBorders() {
    return borders2D(3, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_4_ROW}.
   */
  static @NotNull SlotIterator fourRowsChestBorders() {
    return borders2D(4, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_5_ROW}.
   */
  static @NotNull SlotIterator fiveRowsChestBorders() {
    return borders2D(5, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CHEST_6_ROW}.
   */
  static @NotNull SlotIterator sixRowsChestBorders() {
    return borders2D(6, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#SHULKER_BOX}.
   */
  static @NotNull SlotIterator shulkerBoxBorders() {
    return borders2D(3, 9);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CRAFTER_3X3}
   *     and a {@link InventoryType#WINDOW_3X3}.
   */
  static @NotNull SlotIterator borders3x3() {
    return borders2D(3, 3);
  }

  /**
   * @return a new SlotIterator that calculates the borders of a {@link InventoryType#CRAFTING}.
   */
  static @NotNull SlotIterator craftingTableBorders() {
    return ofIterators(
        horizontally(1, 3), horizontally(7, 3), vertically(4, 1, 3), vertically(6, 1, 3));
  }

  boolean hasNext(@NotNull InventoryType type);

  int next(@NotNull InventoryType type);
}
