package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.audience.MenuViewer;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.page.Pagination;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Schedulable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface Menu<M extends Menu<M, V, T>, V, T extends Inventory> extends Schedulable {

  @Contract("_ -> this")
  M renderTitle(@NotNull Component title);

  @NotNull Component title();

  @NotNull Component renderedTitle();

  @NotNull @UnmodifiableView List<ItemStack> renderedItems();

  @NotNull RelativeItemContainer staticItemContainer();

  @NotNull StackedItemContainer pageableItemContainer();

  int capacity();

  void render(@NotNull V viewer);

  void remove(@NotNull V viewer);

  @NotNull @UnmodifiableView
  Collection<MenuViewer<V>> viewersView();

  @NotNull Pagination<M> pagination();

  @NotNull InventoryType type();

  @NotNull T inventory();

  void tick();
}