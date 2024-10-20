package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.MenuItem;
import com.github.ynverxe.conventionalwindow.item.container.RelativeItemContainer;
import com.github.ynverxe.conventionalwindow.item.container.StackedItemContainer;
import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.page.Pagination;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.Schedulable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * A Menu is an object forwarded by an {@link Inventory} and servers
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
 * {@link SimpleMenu} is the default implementation for this class.
 *
 * @param <M> The Menu implementation class
 * @param <V> The viewer type
 * @param <T> The inventory type, it is up to the programmer's choice.
 */
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

  @NotNull Pagination<M> pagination();

  @NotNull InventoryType type();

  @NotNull T inventory();

  void tick();

  @Contract("_ -> this")
  M configureItemContext(@NotNull Consumer<ItemContext> configurator);

  @NotNull RenderView renderView();

}