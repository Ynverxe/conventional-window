package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
@Internal
public class AirMenuItem implements MenuItem {

  public static final AirMenuItem INSTANCE = new AirMenuItem();

  private AirMenuItem() {}

  @Override
  public @Nullable ItemStack get(@NotNull ItemContext context) {
    return ItemStack.AIR;
  }

  @Override
  public @NotNull ItemClickHandler clickHandler() {
    return ItemClickHandler.cancelClick();
  }

  @Override
  public MenuItem withClickHandler(@NotNull ItemClickHandler clickHandler) {
    return this;
  }

  @Override
  public @NotNull Object copy() {
    return this;
  }
}