package com.github.ynverxe.conventionalwindow.minestom;

import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MinestomMenu<T extends Inventory> extends
    SimpleMenu<MenuItem<InventoryPreClickEvent, ?>, Player, T, MinestomMenu<T>> {

  public MinestomMenu(@NotNull T inventory, @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
  }
}