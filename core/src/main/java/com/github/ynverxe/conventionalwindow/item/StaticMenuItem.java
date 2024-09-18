package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.util.Copyable;
import java.util.Objects;
import java.util.function.Function;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaticMenuItem<E> extends AbstractMenuItem<E, StaticMenuItem<E>, StaticMenuItem<?>> {

  private final int updateTimes;
  private int updateCount = 0;
  private final @NotNull ItemStack itemStack;

  /**
   * Creates a new StaticItemMenu
   *
   * @param updateTimes  The times that this provider will retrieve a non-null item stack. If this
   *                     param less than one, the provider will return the passed item stack
   *                     forever.
   * @param itemStack    The item stack that this provider will retrieve.
   * @param clickHandler The click handler used to handle click events made to this menu item.
   */
  protected StaticMenuItem(int updateTimes, @NotNull ItemStack itemStack, @NotNull ItemClickHandler<E> clickHandler) {
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
   * @return the times that this provider will retrieve a non-null item stack.
   */
  public int updateTimes() {
    return updateTimes;
  }

  @Contract("_ -> new")
  public StaticMenuItem<E> withUpdateTimes(@NotNull ItemClickHandler<E> clickHandler) {
    return create(clickHandler);
  }

  /**
   * @param updateTimes the times that the new provider will retrieve a non-null item stack.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticItemProvider with the passed updateTimes
   */
  @Contract("_ -> new")
  public StaticMenuItem<E> withUpdateTimes(int updateTimes) {
    return newItem(itemStack, updateTimes);
  }

  /**
   * @return The item stack that this provider will retrieve.
   */
  public @NotNull ItemStack itemStack() {
    return itemStack;
  }

  /**
   * @param itemStack the item stack the new provider will retrieve.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticItemProvider with the passed itemStack
   */
  @Contract("_ -> new")
  public StaticMenuItem<E> withItemStack(@NotNull ItemStack itemStack) {
    return newItem(itemStack, updateTimes);
  }

  /**
   * @param provider the item stack function used to get the item stack that the new provider will retrieve.
   *
   * @see StaticMenuItem#StaticMenuItem(int, ItemStack, ItemClickHandler)
   * @return a new StaticItemProvider with the passed itemStack
   */
  @Contract("_ -> new")
  public StaticMenuItem<E> withItemStack(@NotNull Function<@NotNull ItemStack, @NotNull ItemStack> provider) {
    return newItem(provider.apply(itemStack), updateTimes);
  }

  @Override
  public @NotNull StaticMenuItem<E> copy() {
    return newItem(itemStack, updateTimes);
  }

  @SuppressWarnings("unchecked")
  public @NotNull StaticMenuItem<E> newItem(@NotNull ItemStack itemStack, int updateTimes) {
    ItemClickHandler<E> clickHandler = clickHandler();
    if (clickHandler instanceof Copyable<?>) {
      clickHandler = ((Copyable<ItemClickHandler<E>>) clickHandler).copy();
    }

    return new StaticMenuItem<>(updateTimes, itemStack, clickHandler);
  }

  @Override
  protected @NotNull <S> StaticMenuItem<S> create(@NotNull ItemClickHandler<S> clickHandler) {
    return new StaticMenuItem<>(updateTimes, itemStack, clickHandler);
  }
}