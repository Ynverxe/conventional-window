package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.inventory.CustomAdaptableInventory;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitMenu extends SimpleMenu<MenuItem<InventoryClickEvent, ?>, Player, CustomAdaptableInventory, BukkitMenu> {

  public BukkitMenu(@NotNull CustomAdaptableInventory inventory,
      @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
  }
}
