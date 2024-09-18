package com.github.ynverxe.conventionalwindow.test;

import com.github.ynverxe.conventionalwindow.MinestomMenu;
import com.github.ynverxe.conventionalwindow.MinestomMenuContainer;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.SequentialMenuItem;
import com.github.ynverxe.conventionalwindow.item.StaticMenuItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
public class ItemMenuTest {

  private static final MinestomMenuContainer<Inventory> CONTAINER;

  static {
    MinecraftServer.init();
    CONTAINER = new MinestomMenuContainer<>(type -> new Inventory(type, Component.empty()));
  }

  @Test
  public void testOrder() {
    MinestomMenu<Inventory> minestomMenu = CONTAINER.newMenu(InventoryType.CHEST_1_ROW);

    MenuItem<InventoryPreClickEvent, ?> apple = MenuItem.simple(Material.APPLE);
    MenuItem<InventoryPreClickEvent, ?> pane = MenuItem.simple(Material.BLACK_STAINED_GLASS_PANE);

    // Glass pane will be set in slot 0 and apple pushed to slot 1.
    minestomMenu.pageableItemContainer().add(apple);
    assertEquals(apple, minestomMenu.renderView().getItem(0));

    // Now glass pane pushes the apple forward
    minestomMenu.staticItemContainer().put(0, pane);

    assertEquals(apple, minestomMenu.renderView().getItem(1));
    assertEquals(pane, minestomMenu.renderView().getItem(0));
  }

  @Test
  public void testSimpleMenuItem() {
    ItemStack apple = ItemStack.of(Material.APPLE);
    // Create a MenuItem that stop returning non-null item after two calls.
    StaticMenuItem<?> menuItem = MenuItem.simple(apple, 2);

    assertEquals(apple, menuItem.get(null));
    assertEquals(apple, menuItem.get(null));

    assertNull(menuItem.get(null));
  }

  @Test
  public void testSequentialItem() {
    ItemStack iron = ItemStack.of(Material.IRON_INGOT);
    ItemStack gold = ItemStack.of(Material.GOLD_INGOT);
    ItemStack diamond = ItemStack.of(Material.DIAMOND);

    SequentialMenuItem<?> sequentialMenuItem = MenuItem.sequential(iron, 1, gold, 1, diamond, 1);

    assertEquals(iron, sequentialMenuItem.get(null));
    waitMillis(50); // iron -> gold
    assertEquals(gold, sequentialMenuItem.get(null));
    waitMillis(50); // gold -> diamond
    assertEquals(diamond, sequentialMenuItem.get(null));
    waitMillis(50); // diamond -> iron
    assertEquals(iron, sequentialMenuItem.get(null));
  }

  private static void waitMillis(int millis) {
    long timestamp = System.currentTimeMillis() + millis;

    while (timestamp >= System.currentTimeMillis()) {}
  }
}