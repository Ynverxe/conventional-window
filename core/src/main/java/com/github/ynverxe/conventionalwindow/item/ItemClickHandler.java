package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import org.jetbrains.annotations.NotNull;

public interface ItemClickHandler {

  boolean handleItemClick(@NotNull InventoryPreClickEvent event, @NotNull ItemContext context);

  static @NotNull ItemClickHandler cancelClick() {
    return (event,context) -> true;
  }
}