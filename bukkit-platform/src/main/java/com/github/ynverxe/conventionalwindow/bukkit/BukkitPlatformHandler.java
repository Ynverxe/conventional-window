package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.bukkit.internal.player.WrappedMinestomPlayer;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import java.util.Collection;
import net.minestom.server.inventory.Inventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformHandler implements PlatformHandler<Player> {

  /*
  private final org.bukkit.inventory.Inventory backingInventory;

  public BukkitPlatformHandler(org.bukkit.inventory.Inventory backingInventory) {
    this.backingInventory = backingInventory;
  }
   */

  @Override
  public void render(@NotNull Player viewer, @NotNull Inventory inventory) {
    WrappedMinestomPlayer wrappedMinestomPlayer = WrappedMinestomPlayer.compute(viewer);
    wrappedMinestomPlayer.openInventory(inventory);
  }

  @Override
  public void remove(@NotNull Player viewer, @NotNull Inventory inventory) {
    WrappedMinestomPlayer wrappedMinestomPlayer = WrappedMinestomPlayer.compute(viewer);
    wrappedMinestomPlayer.closeInventory();
  }

  @Override
  public Collection<Player> viewers(@NotNull Inventory inventory) {
    /*
     return backingInventory.getViewers().stream()
        .filter(humanEntity -> humanEntity instanceof Player)
        .map(humanEntity -> (Player) humanEntity)
        .toList();
     */

    return inventory.getViewers()
        .stream()
        .map(player -> ((WrappedMinestomPlayer) player).bukkitPlayer())
        .toList();
  }
}
