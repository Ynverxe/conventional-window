package com.github.ynverxe.conventionalwindow.item;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {

  ItemProvider AIR = () -> ItemStack.AIR;

  @Nullable ItemStack get();

  /**
   * Used on fill operations.
   *
   * @return a copy of this provider.
   */
  default @NotNull ItemProvider fork() {
    return this;
  }

  @Contract("_ -> new")
  static ItemProvider of(@NotNull ItemStack itemStack) {
    return () -> itemStack;
  }

  @Contract("_ -> new")
  static ItemProvider of(@NotNull Material material) {
    return () -> ItemStack.of(material);
  }

  static @NotNull DelayedItemProvider delayed() {
    return new DelayedItemProvider();
  }
}