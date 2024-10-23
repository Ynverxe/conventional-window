package com.github.ynverxe.conventionalwindow.test;

import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.MenuContainer;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.SequentialMenuItem;
import com.github.ynverxe.conventionalwindow.item.StaticMenuItem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
public class ItemMenuTest {

  private static final MenuContainer<?> CONTAINER;

  static {
    MinecraftServer.init();
    CONTAINER =
        new MenuContainer<Menu>() {
          @Override
          protected Menu createMenu(@NotNull InventoryType inventoryType) {
            return new Menu(inventoryType);
          }
        };
  }

  @Test
  public void testOrder() {
    Menu<?> minestomMenu = CONTAINER.newMenu(InventoryType.CHEST_1_ROW);

    StaticMenuItem apple = MenuItem.simple(Material.APPLE);
    StaticMenuItem pane = MenuItem.simple(Material.BLACK_STAINED_GLASS_PANE);

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
    // Create a MenuItem that stops returning a non-null item after two calls.
    StaticMenuItem menuItem = MenuItem.simple(apple, 2);

    assertEquals(apple, menuItem.get(null)); // 1st call
    assertEquals(apple, menuItem.get(null)); // 2nd call

    assertNull(menuItem.get(null)); // must be null at the 3rd call
  }

  @Test
  public void testSequentialItem() {
    ItemStack iron = ItemStack.of(Material.IRON_INGOT);
    ItemStack gold = ItemStack.of(Material.GOLD_INGOT);
    ItemStack diamond = ItemStack.of(Material.DIAMOND);

    SequentialMenuItem sequentialMenuItem = MenuItem.sequential(iron, 1, gold, 1, diamond, 1);

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