package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.MenuContainer;
import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.inventory.CustomAdaptableInventory;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BukkitMenuContainer
    extends MenuContainer<
        SimpleMenu<Player, CustomAdaptableInventory>, Player, CustomAdaptableInventory> {

  public BukkitMenuContainer(@NotNull JavaPlugin plugin, @NotNull Logger logger) {
    super(logger);
    Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L);
  }

  public BukkitMenuContainer(@NotNull JavaPlugin plugin) {
    this(plugin, plugin.getComponentLogger());
  }

  @Override
  public @NotNull BukkitMenu newMenu(@NotNull InventoryType inventoryType) {
    return (BukkitMenu) super.newMenu(inventoryType);
  }

  @Override
  protected BukkitMenu createMenu(@NotNull InventoryType inventoryType) {
    BukkitInventoryAdapter inventory =
        new BukkitInventoryAdapter(inventoryType, Component.text(""), (property, value) -> true);

    return new BukkitMenu(inventory, new BukkitPlatformHandler(inventory.bukkitInventory()));
  }
}
