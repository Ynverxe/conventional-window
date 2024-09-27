package com.github.ynverxe.conventionalwindow.bukkit.internal.network;

import com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet.AllowedPackets;
import com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet.CustomInboundAdapter;
import com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet.CustomPacketEncoder;
import com.github.ynverxe.conventionalwindow.bukkit.player.WrappedMinestomPlayer;
import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.net.SocketAddress;
import java.util.Objects;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerConnectionBridge extends PlayerConnection {

  private static final String ENCODER_NAMESPACE = "conventional_window_packet_encoder";
  private static final String DECODER_NAMESPACE = "conventional_window_packet_decoder";

  private final Player player;

  public PlayerConnectionBridge(@NotNull Player player) {
    this.player = Objects.requireNonNull(player, "player");
  }

  @Override
  public void sendPacket(@NotNull SendablePacket sendablePacket) {
    sendablePacket = SendablePacket.extractServerPacket(ConnectionState.PLAY, sendablePacket);

    if (!AllowedPackets.SERVER_TO_CLIENT.containsValue(sendablePacket.getClass())) {
      throw new IllegalArgumentException("Packet '" + sendablePacket.getClass() + "' cannot be sent");
    }

    if (!(sendablePacket instanceof Play playPacket)) {
      return;
    }

    int containerId = getPlayer().bukkitContainerId();
    if (playPacket instanceof OpenWindowPacket openWindowPacket) {
      playPacket = new OpenWindowPacket(
          containerId,
          openWindowPacket.windowType(),
          openWindowPacket.title()
      );
    }

    if (playPacket instanceof WindowItemsPacket windowItemsPacket) {
      playPacket = new WindowItemsPacket(
          windowItemsPacket.windowId(),
          containerId,
          windowItemsPacket.items(),
          windowItemsPacket.carriedItem()
      );
    }

    if (sendablePacket instanceof SetSlotPacket setSlotPacket) {
      playPacket = new SetSlotPacket(
          setSlotPacket.windowId(),
          containerId,
          setSlotPacket.slot(),
          setSlotPacket.itemStack()
      );
    }

    NMSModule.instance().sendPacket(player, playPacket);
  }

  @Override
  public @NotNull SocketAddress getRemoteAddress() {
    return player.getAddress();
  }

  @Override
  public @NotNull WrappedMinestomPlayer getPlayer() {
    net.minestom.server.entity.Player player = super.getPlayer();
    Objects.requireNonNull(player, "player wasn't initialized");
    return (WrappedMinestomPlayer) player;
  }

  @Override
  public void setPlayer(net.minestom.server.entity.Player player) {
    if (!(player instanceof WrappedMinestomPlayer))
      throw new IllegalArgumentException("Player is not an instance of WrappedMinestomPlayer");

    super.setPlayer(player);

    addChannelHandlersIfAbsent(this.player, getPlayer());
  }

  private void addChannelHandlersIfAbsent(@NotNull Player player, @NotNull WrappedMinestomPlayer wrappedMinestomPlayer) {
    ChannelPipeline channelPipeline = NMSModule.instance().getChannelPipeline(player);

    MessageToByteEncoder<?> vanillaPacketEncoder = (MessageToByteEncoder<?>) channelPipeline.get("encoder");
    if (channelPipeline.get(ENCODER_NAMESPACE) == null) {
      channelPipeline.replace("encoder", ENCODER_NAMESPACE, new CustomPacketEncoder(vanillaPacketEncoder));
    }

    ByteToMessageDecoder vanillaPacketDecoder = (ByteToMessageDecoder) channelPipeline.get("decoder");
    if (channelPipeline.get(DECODER_NAMESPACE) == null) {
      channelPipeline.replace("decoder", DECODER_NAMESPACE, new CustomInboundAdapter(vanillaPacketDecoder, wrappedMinestomPlayer));
    }
  }
}