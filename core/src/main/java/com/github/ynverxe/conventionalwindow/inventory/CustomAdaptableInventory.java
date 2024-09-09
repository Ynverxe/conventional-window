package com.github.ynverxe.conventionalwindow.inventory;

import com.github.ynverxe.conventionalwindow.inventory.property.PropertyHandler;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryProperty;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class CustomAdaptableInventory extends Inventory {

  private final PropertyHandler propertyHandler;

  public CustomAdaptableInventory(
      @NotNull InventoryType inventoryType,
      @NotNull Component title,
      @NotNull PropertyHandler propertyHandler) {
    super(inventoryType, title);
    this.propertyHandler = propertyHandler;
  }

  public PropertyHandler propertyHandler() {
    return propertyHandler;
  }

  @Override
  public void sendProperty(@NotNull InventoryProperty property, short value) {
    if (!propertyHandler.handleProperty(property, value)) return;

    super.sendProperty(property, value);
  }
}
