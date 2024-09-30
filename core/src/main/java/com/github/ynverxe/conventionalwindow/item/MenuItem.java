package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.SequentialMenuItem.Entry;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.util.Copyable;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MenuItem<T extends MenuItem<T>> extends Copyable<T> {

  @Nullable ItemStack get(@NotNull ItemContext context);

  @NotNull ItemClickHandler clickHandler();

  @Contract("_ -> new")
  T withClickHandler(@NotNull ItemClickHandler clickHandler);

  /**
   * @param pairs An object array containing the data to be converted into a {@link Entry} list.
   * @return A new SequentialMenuItem with the provided entry data.
   * @see SequentialMenuItem#withEntries(Object...)
   */
  static @NotNull SequentialMenuItem sequential(@NotNull Object... pairs) {
    return SequentialMenuItem.EMPTY.withEntries(pairs);
  }

  /**
   * Collections must be of the same size. The configuration of every {@link Entry} depends
   * on the order of the objects. E.g., the first {@link Entry} of the list will use the first {@link ItemStack} from
   * the itemStacks collection and the first {@link Duration} of the duration collection.
   *
   * @param itemStacks A collection of {@link ItemStack} used to create an {@link Entry} list.
   * @param durations A collection of {@link Duration} used to create an {@link Entry} list.
   * @return A new SequentialMenuItem with the provided entry data.
   * @see SequentialMenuItem#withEntries(Collection, Collection)
   */
  static @NotNull SequentialMenuItem sequential(@NotNull Collection<ItemStack> itemStacks, @NotNull Collection<Duration> durations) {
    return SequentialMenuItem.EMPTY.withEntries(itemStacks, durations);
  }

  /**
   * @param entries The array of {@link Entry} to be consumed by the {@link SequentialMenuItem#SequentialMenuItem(List, ItemClickHandler)} constructor.
   * @return A new SequentialMenuItem with the provided entry data.
   * @param <E> The inventory click event type.
   */
  static @NotNull SequentialMenuItem sequential(@NotNull Entry... entries) {
    return new SequentialMenuItem(Arrays.asList(entries), ItemClickHandler.cancelClick());
  }

  /**
   * @param itemStack The ItemStack to be used.
   * @param updateTimes The times that the ItemStack can be accessed.
   * @return a new StaticMenuItem.
   */
  static @NotNull StaticMenuItem simple(@NotNull ItemStack itemStack, int updateTimes) {
    return new StaticMenuItem(updateTimes, itemStack, ItemClickHandler.cancelClick());
  }

  /**
   * @param itemStack The ItemStack to be used.
   * @return a new StaticMenuItem that retrieves the provided ItemStack always.
   * @param <E> The inventory click event type.
   */
  static @NotNull StaticMenuItem simple(@NotNull ItemStack itemStack) {
    return simple(itemStack, -1);
  }

  /**
   * @param material The material used to create a new ItemStack.
   * @param updateTimes The times that the ItemStack can be accessed.
   * @return a new StaticMenuItem
   * @param <E> The inventory click event type.
   */
  static @NotNull StaticMenuItem simple(@NotNull Material material, int updateTimes) {
    return simple(ItemStack.of(material), updateTimes);
  }

  /**
   * @param material The material used to create a new ItemStack.
   * @return a new StaticMenuItem that retrieves the created ItemStack always.
   * @param <E> The inventory click event type.
   */
  static @NotNull StaticMenuItem simple(@NotNull Material material) {
    return simple(material, -1);
  }
}