package com.github.ynverxe.conventionalwindow.bukkit.player;

import com.github.ynverxe.conventionalwindow.bukkit.internal.network.PlayerConnectionBridge;
import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import com.github.ynverxe.conventionalwindow.audience.ResolvableAudience;
import java.util.Optional;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

public class WrappedMinestomPlayer extends Player implements ResolvableAudience {

  /**
   * This value can be outdated sometimes. It is only updated when calling {@link #openInventory(Inventory)}
   */
  @Internal
  private int bukkitContainerId;
  private final @NotNull org.bukkit.entity.Player bukkitPlayer;
  @Internal
  public boolean sendClosePacket = true;

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

  public boolean hasBukkitInventoryOpen() {
    return bukkitPlayer.getOpenInventory().getType() != InventoryType.CRAFTING;
  }

  @Internal
  public void closeInventorySilently() {
    this.sendClosePacket = false;
    this.closeInventory();
  }

  @Override
  public @NotNull Audience audience() {
    return bukkitPlayer;
  }

  public static @NotNull Optional<WrappedMinestomPlayer> fromPlayer(@NotNull org.bukkit.entity.Player player) {
    return Optional.ofNullable(WrappedPlayerCache.INSTANCE.get(player));
  }
}