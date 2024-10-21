package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.page.Pagination;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Schedulable;
import net.minestom.server.timer.Scheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * A Menu is an object that extends {@link Inventory} and serves
 * as a nexus for a lot of other components.
 * They are:
 * <ul>
 *   <li>A {@link RelativeItemContainer} that stores static items.</li>
 *   <li>A {@link StackedItemContainer} that stores pageable items.</li>
 *   <li>A {@link Pagination} that handles page operations.</li>
 *   <li>An {@link ItemRenderer} that distributes and render the static and pageable items.</li>
 *   <li>An {@link Inventory}, this guy doesn't needs a presentation.</li>
 *   <li>A sequence of generic type parameters</li>
 * </ul>
 *
 * @param <M> The Menu implementation class
 * @param <V> The viewer type
 */
@SuppressWarnings("unchecked, UnusedReturnValue, unused")
public class Menu<M extends Menu<M, V>, V extends Player> extends Inventory implements Schedulable {

  private final List<ItemStack> renderedItemStacks = new ArrayList<>();
  private final StackedItemContainer pageableItems;
  private final RelativeItemContainer staticItems;
  private final ItemRenderer itemRenderer;
  private final Pagination<M> pagination;

  private final @NotNull Scheduler scheduler = Scheduler.newScheduler();

  private volatile @NotNull Consumer<ItemContext> itemContextConfigurator = context -> {};

  public Menu(@NotNull InventoryType inventoryType) {
    super(inventoryType, Component.empty());
    this.itemRenderer = new ItemRenderer(this, capacity());
    this.pageableItems = new StackedItemContainer(this, this.itemRenderer);
    this.staticItems = new RelativeItemContainer(this, this.itemRenderer, getSize());
    this.pagination = new Pagination<>((M) this, itemRenderer);
    this.itemRenderer.init();
  }

  @Contract("_ -> this")
  public M renderTitle(@NotNull Component title) {
    this.setTitle(title);
    return (M) this;
  }

  public @NotNull @UnmodifiableView List<ItemStack> renderedItems() {
    return Collections.unmodifiableList(renderedItemStacks);
  }

  public @NotNull RelativeItemContainer staticItemContainer() {
    return staticItems;
  }

  public @NotNull StackedItemContainer pageableItemContainer() {
    return pageableItems;
  }

  public int capacity() {
    return this.getSize();
  }

  @NotNull
  public Pagination<M> pagination() {
    return pagination;
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

  public M configureItemContext(@NotNull Consumer<ItemContext> configurator) {
    this.itemContextConfigurator = this.itemContextConfigurator.andThen(
        Objects.requireNonNull(configurator, "configurator"));
    return (M) this;
  }

  public @NotNull RenderView renderView() {
    return itemRenderer;
  }

  @Override
  public @NotNull Scheduler scheduler() {
    return scheduler;
  }
}