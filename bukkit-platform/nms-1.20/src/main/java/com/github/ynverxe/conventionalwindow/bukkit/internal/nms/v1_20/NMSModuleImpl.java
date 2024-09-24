package com.github.ynverxe.conventionalwindow.bukkit.internal.nms.v1_20;

import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import io.netty.channel.ChannelPipeline;
import net.minestom.server.network.packet.server.ServerPacket;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSModuleImpl extends NMSModule {

  @Override
  public @NotNull ChannelPipeline getChannelPipeline(@NotNull Player player) {
    return ((CraftPlayer) player).getHandle()
        .connection
        .connection
        .channel
        .pipeline();
  }

  @Override
  public void sendPacket(@NotNull Player player, @NotNull ServerPacket.Play playPacket) {
    ((CraftPlayer) player).getHandle().connection.send(new PacketWrapper(playPacket));
  }

  @Override
  public int incrementContainerId(@NotNull Player player) {
    return ((CraftPlayer) player)
        .getHandle()
        .nextContainerCounter();
  }
}