package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.ItemClickHandler;
import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.page.Pagination;
import com.github.ynverxe.conventionalwindow.platform.PlatformHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public abstract class SimpleMenu<I extends MenuItem<?, ?>, V, C extends Inventory, T extends SimpleMenu<I, V, C, T>>
    implements Menu<I, T, V, C> {

  private final C inventory;
  private final PlatformHandler<V> platformHandler;
  private final List<ItemStack> renderedItemStacks = new ArrayList<>();
  private final StackedItemContainer<I> pageableItems;
  private final RelativeItemContainer<I> staticItems;
  private final ItemRenderer<I> itemRenderer;
  private final Pagination<T> pagination;

  private final @NotNull Scheduler scheduler = Scheduler.newScheduler();

  private volatile @NotNull Component title = Component.empty();
  private volatile @NotNull Component renderedTitle = Component.empty();
  private volatile @NotNull Consumer<ItemContext> itemContextConfigurator = context -> {};

  public SimpleMenu(@NotNull C inventory, @NotNull PlatformHandler<V> platformHandler) {
    this.inventory = inventory;
    this.platformHandler = platformHandler;
    this.itemRenderer = new ItemRenderer<>(this, capacity());
    this.pageableItems = new StackedItemContainer<>(this, this.itemRenderer);
    this.staticItems = new RelativeItemContainer<>(this, this.itemRenderer, inventory.getSize());
    this.pagination = new Pagination<>((T) this, itemRenderer);
    this.itemRenderer.init();
  }

  @Override
  public T renderTitle(@NotNull Component title) {
    this.title = title;
    this.renderedTitle = title;
    this.inventory.setTitle(renderedTitle);
    return (T) this;
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
  public @NotNull RelativeItemContainer<I> staticItemContainer() {
    return staticItems;
  }

  @Override
  public @NotNull StackedItemContainer<I> pageableItemContainer() {
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
  public Pagination<T> pagination() {
    return pagination;
  }

  @Override
  @NotNull
  public InventoryType type() {
    return inventory.getInventoryType();
  }

  @Override
  public @NotNull C inventory() {
    return inventory;
  }

  public void tick() {
    ItemContext itemContext = createItemContext();
    this.itemRenderer.updateItems(itemContext);
    this.scheduler.processTick();
  }

  protected ItemContext createItemContext() {
    ItemContext itemContext = new ItemContext(this);
    itemContextConfigurator.accept(itemContext);
    return itemContext;
  }

  @Override
  public T configureItemContext(@NotNull Consumer<ItemContext> configurator) {
    this.itemContextConfigurator = this.itemContextConfigurator.andThen(Objects.requireNonNull(configurator, "configurator"));
    return (T) this;
  }

  @Override
  public @NotNull RenderView<I> renderView() {
    return itemRenderer;
  }

  @Override
  public @NotNull Scheduler scheduler() {
    return scheduler;
  }

}