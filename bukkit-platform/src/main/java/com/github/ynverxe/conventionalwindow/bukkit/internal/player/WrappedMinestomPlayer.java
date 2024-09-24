package com.github.ynverxe.conventionalwindow.bukkit.internal.player;

import com.github.ynverxe.conventionalwindow.bukkit.internal.network.PlayerConnectionBridge;
import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import java.util.Optional;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public class WrappedMinestomPlayer extends Player {

  /**
   * This value can be outdated sometimes. It is only updated when calling {@link #openInventory(Inventory)}
   */
  @Internal
  private int bukkitContainerId;
  private final @NotNull org.bukkit.entity.Player bukkitPlayer;

  public WrappedMinestomPlayer(@NotNull org.bukkit.entity.Player bukkitPlayer) {
    super(bukkitPlayer.getUniqueId(), bukkitPlayer.getName(), new PlayerConnectionBridge(bukkitPlayer));
    this.bukkitPlayer = bukkitPlayer;
  }

  public @NotNull org.bukkit.entity.Player bukkitPlayer() {
    return bukkitPlayer;
  }

  public static @NotNull WrappedMinestomPlayer compute(@NotNull org.bukkit.entity.Player player) {
    return WrappedPlayerCache.INSTANCE.compute(player);
  }

  @Internal
  public int bukkitContainerId() {
    return bukkitContainerId;
  }

  @Override
  public @NotNull PlayerConnectionBridge getPlayerConnection() {
    return (PlayerConnectionBridge) super.getPlayerConnection();
  }

  @Override
  public boolean openInventory(@NotNull Inventory inventory) {
    if (bukkitPlayer.getOpenInventory().getType() != InventoryType.CRAFTING) {
      bukkitPlayer.closeInventory();
    }

    this.bukkitContainerId = NMSModule.instance().incrementContainerId(bukkitPlayer);

    return super.openInventory(inventory);
  }

  public boolean isMenuOpen() {
    return getOpenInventory() != null;
  }

  public static @NotNull Optional<WrappedMinestomPlayer> fromPlayer(@NotNull org.bukkit.entity.Player player) {
    return Optional.ofNullable(WrappedPlayerCache.INSTANCE.get(player));
  }
}