package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import net.minestom.server.inventory.Inventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitMenu<T extends Inventory> extends SimpleMenu<Player, T> {

  public BukkitMenu(
      @NotNull T inventory,
      @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
  }
}
