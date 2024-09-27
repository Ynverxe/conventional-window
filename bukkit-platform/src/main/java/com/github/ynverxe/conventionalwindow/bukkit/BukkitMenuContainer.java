package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.MenuContainer;
import com.github.ynverxe.conventionalwindow.SimpleMenu;
import java.util.Objects;
import java.util.function.Function;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BukkitMenuContainer<T extends Inventory>
    extends MenuContainer<
        SimpleMenu<Player, T>, Player, T> {

  private final @NotNull Function<@NotNull InventoryType, @NotNull T> inventoryFactory;

  public BukkitMenuContainer(@NotNull JavaPlugin plugin, @NotNull Logger logger,
      @NotNull Function<@NotNull InventoryType, @NotNull T> inventoryFactory) {
    super(logger);
    this.inventoryFactory = Objects.requireNonNull(inventoryFactory, "inventoryFactory");
    Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L);
  }

  public BukkitMenuContainer(@NotNull JavaPlugin plugin,
      @NotNull Function<InventoryType, T> inventoryFactory) {
    this(plugin, plugin.getComponentLogger(), inventoryFactory);
  }

  @Override
  public @NotNull BukkitMenu<T> newMenu(@NotNull InventoryType inventoryType) {
    return (BukkitMenu<T>) super.newMenu(inventoryType);
  }

  @Override
  protected BukkitMenu<T> createMenu(@NotNull InventoryType inventoryType) {
    T inventory = inventoryFactory.apply(inventoryType);

    return new BukkitMenu<>(inventory, new BukkitPlatformHandler());
  }
}
