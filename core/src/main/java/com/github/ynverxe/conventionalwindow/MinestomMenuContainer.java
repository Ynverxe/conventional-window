package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import java.util.Objects;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class MinestomMenuContainer<T extends Inventory>
    extends MenuContainer<MinestomMenu<T>, Player, T> {

  private final Function<InventoryType, T> inventoryFactory;

  public MinestomMenuContainer(
      @NotNull Logger logger, Function<InventoryType, T> inventoryFactory) {
    super(logger);
    this.inventoryFactory = Objects.requireNonNull(inventoryFactory, "inventoryFactory");

    ServerProcess process = MinecraftServer.process();
    Objects.requireNonNull(process, "Cannot get ServerProcess");

    process.scheduler().buildTask(this).repeat(TaskSchedule.tick(1)).schedule();
  }

  public MinestomMenuContainer(@NotNull Function<InventoryType, T> inventoryFactory) {
    this(ComponentLogger.logger(), inventoryFactory);
  }

  public static @NotNull MinestomMenuContainer<Inventory> create() {
    return new MinestomMenuContainer<>(type -> new Inventory(type, Component.text("")));
  }

  @Override
  protected MinestomMenu<T> createMenu(@NotNull InventoryType inventoryType) {
    return new MinestomMenu<>(inventoryFactory.apply(inventoryType), PlatformHandler.minestom());
  }
}