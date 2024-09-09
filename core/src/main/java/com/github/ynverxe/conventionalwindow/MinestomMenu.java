package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MinestomMenu<T extends Inventory> extends SimpleMenu<Player, T> {

  public MinestomMenu(@NotNull T inventory, @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
  }
}
