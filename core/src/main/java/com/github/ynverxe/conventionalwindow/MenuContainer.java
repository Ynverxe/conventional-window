package com.github.ynverxe.conventionalwindow;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public abstract class MenuContainer<M extends Menu<M, V>, V extends Player>
    implements Runnable {

  private final Logger logger;
  private final Set<M> menus = ConcurrentHashMap.newKeySet();

  public MenuContainer(@NotNull Logger logger) {
    this.logger = Objects.requireNonNull(logger, "logger");
  }

  public MenuContainer() {
    this(ComponentLogger.logger());
  }

  public @NotNull M newMenu(@NotNull InventoryType inventoryType) {
    M menu = createMenu(inventoryType);
    menus.add(menu);
    return menu;
  }

  protected abstract M createMenu(@NotNull InventoryType inventoryType);

  @Override
  public void run() {
    for (M menu : menus) {
      try {
        menu.tick();
      } catch (Exception e) {
        logger.error("Cannot tick menu", e);
      }
    }
  }
}
