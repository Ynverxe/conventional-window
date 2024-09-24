package com.github.ynverxe.conventionalwindow.bukkit.nms.common;

import io.netty.channel.ChannelPipeline;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class NMSModule {

  public abstract @NotNull ChannelPipeline getChannelPipeline(@NotNull Player player);

  public abstract void sendPacket(@NotNull Player player, @NotNull Play playPacket);

  public abstract int incrementContainerId(@NotNull Player player);

  public static @NotNull NMSModule instance() {
    return NMSModuleContainer.INSTANCE.convenientModule();
  }
}