package com.github.ynverxe.conventionalwindow.platform;

import com.github.ynverxe.conventionalwindow.audience.MenuViewer;
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
      public Collection<MenuViewer<Player>> viewers(@NotNull Inventory inventory) {
        return inventory.getViewers()
            .stream()
            .map(player -> new MenuViewer<>(player, player))
            .toList();
      }

      @Override
      public @NotNull MenuViewer<Player> newViewer(@NotNull Player player) {
        return new MenuViewer<>(player, player);
      }
    };
  }

  void render(@NotNull V viewer, @NotNull Inventory inventory);

  void remove(@NotNull V viewer, @NotNull Inventory inventory);

  Collection<MenuViewer<V>> viewers(@NotNull Inventory inventory);

  @NotNull MenuViewer<V> newViewer(@NotNull Player player);

}
