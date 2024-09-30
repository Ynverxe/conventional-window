package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.util.Copyable;
import java.util.Objects;
import java.util.function.Function;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaticMenuItem extends AbstractMenuItem<StaticMenuItem> {

  private final int updateTimes;
  private int updateCount = 0;
  private final @NotNull ItemStack itemStack;

  /**
   * Creates a new StaticItemMenu
   *
   * @param updateTimes  The times that this menuItem will retrieve a non-null item stack. If this
   *                     param less than one, the menuItem will return the passed item stack
   *                     forever.
   * @param itemStack    The item stack that this menuItem will retrieve.
   * @param clickHandler The click handler used to handle click events made to this menu item.
   */
  protected StaticMenuItem(int updateTimes, @NotNull ItemStack itemStack, @NotNull ItemClickHandler clickHandler) {
    super(clickHandler);
    this.updateTimes = updateTimes;
    this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
  }

  @Override
  public @Nullable ItemStack get(@NotNull ItemContext context) {
    // infinitely updated
    if (updateTimes <= 0) return itemStack;

    if (updateCount >= updateTimes) {
      return null;
    }

    updateCount++;
    return itemStack;
  }

  /**
   * @return the times that this menuItem will retrieve a non-null item stack.
   */
  public int updateTimes() {
    return updateTimes;
  }

  @Contract("_ -> new")
  public StaticMenuItem withUpdateTimes(@NotNull ItemClickHandler clickHandler) {
    return create(clickHandler);
  }

  /**
   * @param updateTimes the times that the new menuItem will retrieve a non-null item stack.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticMenuItem with the passed updateTimes
   */
  @Contract("_ -> new")
  public StaticMenuItem withUpdateTimes(int updateTimes) {
    return newItem(itemStack, updateTimes);
  }

  /**
   * @return The item stack that this menuItem will retrieve.
   */
  public @NotNull ItemStack itemStack() {
    return itemStack;
  }

  /**
   * @param itemStack the item stack the new menuItem will retrieve.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticMenuItem with the passed itemStack
   */
  @Contract("_ -> new")
  public StaticMenuItem withItemStack(@NotNull ItemStack itemStack) {
    return newItem(itemStack, updateTimes);
  }

  /**
   * @param menuItem the item stack function used to get the item stack that the new menuItem will retrieve.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticMenuItem with the passed itemStack
   */
  @Contract("_ -> new")
  public StaticMenuItem withItemStack(@NotNull Function<@NotNull ItemStack, @NotNull ItemStack> menuItem) {
    return newItem(menuItem.apply(itemStack), updateTimes);
  }

  @Override
  public @NotNull StaticMenuItem copy() {
    return newItem(itemStack, updateTimes);
  }

  @SuppressWarnings("unchecked")
  public @NotNull StaticMenuItem newItem(@NotNull ItemStack itemStack, int updateTimes) {
    ItemClickHandler clickHandler = clickHandler();
    if (clickHandler instanceof Copyable<?>) {
      clickHandler = ((Copyable<ItemClickHandler>) clickHandler).copy();
    }

    return new StaticMenuItem(updateTimes, itemStack, clickHandler);
  }

  @Override
  protected @NotNull StaticMenuItem create(@NotNull ItemClickHandler clickHandler) {
    return new StaticMenuItem(updateTimes, itemStack, clickHandler);
  }
}