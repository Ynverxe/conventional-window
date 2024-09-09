package com.github.ynverxe.conventionalwindow.inventory.property;

import net.minestom.server.inventory.InventoryProperty;
import org.jetbrains.annotations.NotNull;

public interface PropertyHandler {

  boolean handleProperty(@NotNull InventoryProperty property, short value);
}
