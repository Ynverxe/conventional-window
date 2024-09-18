package com.github.ynverxe.conventionalwindow.minestom;

import com.github.ynverxe.conventionalwindow.SimpleMenu;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MinestomMenu<T extends Inventory> extends
    SimpleMenu<MenuItem<InventoryPreClickEvent, ?>, Player, T, MinestomMenu<T>> {

  private final EventListener<InventoryPreClickEvent> inventoryPreClickEventListener = EventListener.of(
      InventoryPreClickEvent.class,
      inventoryPreClickEvent -> {
        int slot = inventoryPreClickEvent.getSlot();
        MenuItem<InventoryPreClickEvent, ?> menuItem = renderView().getItem(slot);
        if (menuItem == null) return;

        ItemContext itemContext = createItemContext();

        boolean cancel = menuItem.clickHandler().handleItemClick(inventoryPreClickEvent, itemContext);
        inventoryPreClickEvent.setCancelled(cancel);
      }
  );

  public MinestomMenu(@NotNull T inventory, @NotNull PlatformHandler<Player> platformHandler) {
    super(inventory, platformHandler);
    MinecraftServer.getGlobalEventHandler().addListener(inventoryPreClickEventListener);
  }

  public @NotNull EventListener<InventoryPreClickEvent> inventoryPreClickEventListener() {
    return inventoryPreClickEventListener;
  }
}