package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface AbstractItemContainer {

  /**
   * Returns the ItemProvider associated with the specified index (from zero to {@link #maxFindableIndex()}).
   *
   * @param index Depending on the implementation, this can be an element position or a key.
   * @return The found ItemProvider
   *
   * @see StackedItemContainer
   * @see RelativeItemContainer
   * @see ItemProvider
   *
   * @throws IndexOutOfBoundsException Depending on the implementation, this exception
   * can be thrown if the provided int identifier is less than zero, it's expected to be an item position and is higher
   * than the stored elements in the container or when it's expected to be a key
   * and is higher than the container capacity.
   */
  @NotNull ItemProvider get(int index) throws IndexOutOfBoundsException;

  /**
   * @return the stored element count or a fixed number on {@link com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer}
   */
  int maxFindableIndex();

  int count();

  int nonNullCount();

  @Contract("_, _ -> this")
  AbstractItemContainer fill(@NotNull SlotIterator iterator, @NotNull ItemProvider provider);

}