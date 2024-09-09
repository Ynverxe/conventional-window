package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.inventory.CustomAdaptableInventory;
import com.github.ynverxe.conventionalwindow.inventory.property.PropertyHandler;
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.MenuType;
import net.minestom.server.inventory.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class BukkitInventoryAdapter extends CustomAdaptableInventory {

  private final Inventory bukkitInventory;

  public BukkitInventoryAdapter(
      @NotNull InventoryType inventoryType,
      @NotNull Component title,
      @NotNull PropertyHandler propertyHandler) {
    super(inventoryType, title, propertyHandler);

    if (inventoryType.name().contains("CHEST")) {
      this.bukkitInventory = Bukkit.createInventory(null, inventoryType.getSize());
      return;
    }

    this.bukkitInventory =
        Bukkit.createInventory(
            null, org.bukkit.event.inventory.InventoryType.valueOf(inventoryType.name()));
  }

  @Override
  public void setTitle(@NotNull Component title) {
    super.setTitle(title);

    int windowId = getWindowId();

    for (HumanEntity viewer : bukkitInventory.getViewers()) {
      ServerPlayer player = ((CraftPlayer) viewer).getHandle();
      ServerGamePacketListenerImpl listener = player.connection;

      MenuType<?> menuType = player.containerMenu.getType();

      ClientboundOpenScreenPacket openPacket =
          new ClientboundOpenScreenPacket(windowId, menuType, new AdventureComponent(title));
      listener.sendPacket(openPacket);

      int slot = 0;
      for (net.minecraft.world.item.ItemStack content :
          ((CraftInventory) bukkitInventory).getInventory().getContents()) {
        if (content != null) {
          ClientboundContainerSetSlotPacket packet =
              new ClientboundContainerSetSlotPacket(windowId, 0, slot, content);
          listener.sendPacket(packet);
        }
        slot++;
      }
    }
  }

  public Inventory bukkitInventory() {
    return bukkitInventory;
  }
}
