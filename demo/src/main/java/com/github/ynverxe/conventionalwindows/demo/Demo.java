package com.github.ynverxe.conventionalwindows.demo;

import com.github.ynverxe.conventionalwindow.MinestomMenuContainer;
import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class Demo {
  public static void main(String[] args){
    MinecraftServer server = MinecraftServer.init();
    server.start("localhost", 25565);

    InstanceContainer container = MinecraftServer.getInstanceManager().createInstanceContainer();

    MinestomMenuContainer<Inventory> menuContainer = MinestomMenuContainer.create();

    SimpleMenu<Player, Inventory> menu = menuContainer.newMenu(InventoryType.CHEST_6_ROW);

    menu.staticItemContainer()
        .fill(SlotIterator.sixRowsChestBorders(), ItemProvider.of(Material.BLACK_STAINED_GLASS_PANE));

    ItemProvider provider = ItemProvider.delayed()
        .add(ItemStack.of(Material.DIAMOND).withCustomName(
            Component.text("Hi! I'm a diamond :D", NamedTextColor.AQUA)), 20)
        .modifyLast(itemStack -> itemStack.withCustomName(Component.text("I LIED! I'm an emerald >:D",
            NamedTextColor.RED)).withMaterial(Material.EMERALD), 20);

    menu.pageableItemContainer().add(provider);

    menu.scheduler().buildTask(() -> {
      int random = ThreadLocalRandom.current().nextInt();
      menu.renderTitle(Component.text("Random number: " + random));
    }).repeat(TaskSchedule.tick(1)).schedule();

    MinecraftServer.getGlobalEventHandler().addListener(
        AsyncPlayerConfigurationEvent.class, event -> event.setSpawningInstance(container));

    MinecraftServer.getGlobalEventHandler()
        .addListener(
            PlayerSpawnEvent.class,
            event -> {
              event.getPlayer().setAllowFlying(true);
              event.getPlayer().setFlying(true);

              menu.render(event.getPlayer());
            });

    MinecraftServer.getGlobalEventHandler()
        .addListener(InventoryCloseEvent.class, event -> {
          event.getPlayer().closeInventory();
          //menu.scheduler().scheduleNextTick(() -> event.getPlayer().closeInventory());
        });
  }
}