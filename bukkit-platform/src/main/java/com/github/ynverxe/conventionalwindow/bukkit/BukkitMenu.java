package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.inventory.CustomAdaptableInventory;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitMenu extends SimpleMenu<Player, CustomAdaptableInventory> {

  public BukkitMenu(
      @NotNull CustomAdaptableInventory inventory,
      @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
  }
}
