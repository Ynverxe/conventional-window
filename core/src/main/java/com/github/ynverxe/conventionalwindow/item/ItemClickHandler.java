package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import org.jetbrains.annotations.NotNull;

public interface ItemClickHandler<E> {

  boolean handleItemClick(@NotNull E event, @NotNull ItemContext context);

  static <E> @NotNull ItemClickHandler<E> cancelClick() {
    return (event,context) -> true;
  }
}