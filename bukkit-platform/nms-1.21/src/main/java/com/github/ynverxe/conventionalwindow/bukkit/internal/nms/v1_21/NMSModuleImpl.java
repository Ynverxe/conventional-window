package com.github.ynverxe.conventionalwindow.bukkit.internal.nms.v1_21;

import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import io.netty.channel.ChannelPipeline;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.reflect.Field;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.IdDispatchCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.MinecraftServer;
import net.minestom.server.network.packet.server.ServerPacket;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSModuleImpl extends NMSModule {

  private static final Field BY_ID_FIELD;

  static {
    try {
      BY_ID_FIELD = IdDispatchCodec.class
          .getDeclaredField("toId");

      BY_ID_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private final @NotNull Object2IntMap<?> idByPacket;

  public NMSModuleImpl() throws IllegalAccessException {
    IdDispatchCodec<?, ?, ?> gameProtocolCodec = (IdDispatchCodec<?, ?, ?>) GameProtocols.CLIENTBOUND_TEMPLATE
        .bind(byteBuf -> new RegistryFriendlyByteBuf(byteBuf, MinecraftServer.getServer().registryAccess()))
        .codec();

    this.idByPacket = (Object2IntMap<?>) BY_ID_FIELD.get(gameProtocolCodec);
  }

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
    ((CraftPlayer) player).getHandle().connection.sendPacket(new PacketWrapper(playPacket));
  }

  @Override
  public int incrementContainerId(@NotNull Player player) {
    return ((CraftPlayer) player)
        .getHandle()
        .nextContainerCounter();
  }

  @Override
  public int packetId(@NotNull Object packet) {
    Packet<?> mcPacket = (Packet<?>) packet;

    if (mcPacket.type().flow() == PacketFlow.SERVERBOUND) {
      throw new IllegalArgumentException("Cannot handle server-bound packet");
    }

    return idByPacket.getInt(mcPacket);
  }
}