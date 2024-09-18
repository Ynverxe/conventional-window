package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.page.Pagination;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public class SimpleMenu<V, T extends Inventory> implements Menu<SimpleMenu<V, T>, V, T> {

  private final T inventory;
  private final PlatformHandler<V> platformHandler;
  private final List<ItemStack> renderedItemStacks = new ArrayList<>();
  private final StackedItemContainer pageableItems;
  private final RelativeItemContainer staticItems;
  private final ItemRenderer<V> itemRenderer;
  private final Pagination<SimpleMenu<V, T>> pagination;

  private final @NotNull Scheduler scheduler = Scheduler.newScheduler();

  private volatile @NotNull Component title = Component.empty();
  private volatile @NotNull Component renderedTitle = Component.empty();
  private volatile @NotNull Consumer<ItemContext> itemContextConfigurator = context -> {};

  public SimpleMenu(@NotNull T inventory, @NotNull PlatformHandler<V> platformHandler) {
    this.inventory = inventory;
    this.platformHandler = platformHandler;
    this.itemRenderer = new ItemRenderer<>(this, platformHandler, capacity());
    this.pageableItems = new StackedItemContainer(this, this.itemRenderer);
    this.staticItems = new RelativeItemContainer(this, this.itemRenderer, inventory.getSize());
    this.pagination = new Pagination<>(this, itemRenderer);
    this.itemRenderer.init();
  }

  @Override
  public SimpleMenu<V, T> renderTitle(@NotNull Component title) {
    this.title = title;
    this.renderedTitle = title;
    this.inventory.setTitle(renderedTitle);
    return this;
  }

  @Override
  public @NotNull Component title() {
    return this.title;
  }

  @Override
  public @NotNull Component renderedTitle() {
    return this.renderedTitle;
  }

  @Override
  public @NotNull @UnmodifiableView List<ItemStack> renderedItems() {
    return Collections.unmodifiableList(renderedItemStacks);
  }

  @Override
  public @NotNull RelativeItemContainer staticItemContainer() {
    return staticItems;
  }

  @Override
  public @NotNull StackedItemContainer pageableItemContainer() {
    return pageableItems;
  }

  @Override
  public int capacity() {
    return this.inventory.getSize();
  }

  @Override
  public void render(@NotNull V viewer) {
    platformHandler.render(viewer, inventory);
  }

  @Override
  public void remove(@NotNull V viewer) {
    platformHandler.remove(viewer, inventory);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<V> viewersView() {
    return Collections.unmodifiableCollection(platformHandler.viewers(inventory));
  }

  @Override
  @NotNull
  public Pagination<SimpleMenu<V, T>> pagination() {
    return pagination;
  }

  @Override
  @NotNull
  public InventoryType type() {
    return inventory.getInventoryType();
  }

  @Override
  public @NotNull T inventory() {
    return inventory;
  }

  public void tick() {
    this.itemRenderer.updateItems();
    this.scheduler.processTick();
  }

  @Override
  public T configureItemContext(@NotNull Consumer<ItemContext> configurator) {
    this.itemContextConfigurator = this.itemContextConfigurator.andThen(Objects.requireNonNull(configurator, "configurator"));
    return (T) this;
  }
  @Override
  public @NotNull Scheduler scheduler() {
    return scheduler;
  }
}