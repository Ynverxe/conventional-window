package com.github.ynverxe.conventionalwindow.platform;

import java.util.Collection;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface PlatformHandler<V> {

  static @NotNull PlatformHandler<Player> minestom() {
    return new PlatformHandler<>() {
      @Override
      public void render(@NotNull Player viewer, @NotNull Inventory inventory) {
        viewer.openInventory(inventory);
      }

      @Override
      public void remove(@NotNull Player viewer, @NotNull Inventory inventory) {
        viewer.closeInventory();
      }

      @Override
      public Collection<Player> viewers(@NotNull Inventory inventory) {
        return inventory.getViewers();
      }
    };
  }

  void render(@NotNull V viewer, @NotNull Inventory inventory);

  void remove(@NotNull V viewer, @NotNull Inventory inventory);

  Collection<V> viewers(@NotNull Inventory inventory);
}
