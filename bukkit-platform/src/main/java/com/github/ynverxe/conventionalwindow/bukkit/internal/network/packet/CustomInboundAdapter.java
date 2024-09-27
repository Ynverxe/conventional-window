package com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet;

import com.github.ynverxe.conventionalwindow.bukkit.internal.network.PlayerConnectionBridge;
import com.github.ynverxe.conventionalwindow.bukkit.player.WrappedMinestomPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.utils.binary.BinaryBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ALL")
public class CustomInboundAdapter extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger("CustomInboundAdapter");
  private static final PacketListenerManager PACKET_LISTENER_MANAGER = new PacketListenerManager();
  private static final PacketProcessor PACKET_PROCESSOR = new PacketProcessor(PACKET_LISTENER_MANAGER);

  private final @NotNull ChannelInboundHandlerAdapter vanillaDelegate;
  private @Nullable BinaryBuffer incompletePacket;
  private final @NotNull WrappedMinestomPlayer player;
  private final @NotNull PlayerConnectionBridge playerConnection;

  public CustomInboundAdapter(@NotNull ChannelInboundHandlerAdapter vanillaDelegate, @NotNull WrappedMinestomPlayer player) {
    this.vanillaDelegate = Objects.requireNonNull(vanillaDelegate, "vanillaDelegate");
    this.player = Objects.requireNonNull(player, "player");
    this.playerConnection = player.getPlayerConnection();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!player.isMenuOpen()) {
      //System.out.println("Received packet, but menu isn't open ");
      vanillaDelegate.channelRead(ctx, msg);
      return;
    }

    if (!(msg instanceof ByteBuf byteBuf)) {
      //System.out.println("Received packet, but msg isn't a ByteBuf ");
      vanillaDelegate.channelRead(ctx, msg);
      return;
    }

    //System.out.println("Received packet");

    try {
      ByteBuffer nioBuffer = byteBuf.nioBuffer();
      NetworkBuffer buffer = new NetworkBuffer(nioBuffer, false);
      int length = buffer.read(NetworkBuffer.VAR_INT);
      int packetId = buffer.read(buffer.VAR_INT);

      //System.out.println("Processing packet: " + packetId);

      try {
        if (!AllowedPackets.CLIENT_TO_SERVER.containsKey(packetId)) {
          //System.out.println("Packet delegated to vanilla");

          vanillaDelegate.channelRead(ctx, msg);
          return;
        }

        ByteBuffer packetBodyBuffer = nioBuffer.slice(buffer.readIndex(), length);
        PACKET_PROCESSOR.process(playerConnection, packetId, packetBodyBuffer);
      } catch (Exception e) {
        LOGGER.error("Unexpected error when processing packet(id={})", packetId, e);
      }
    } catch (Exception e) {
      LOGGER.error("Unexpected error", e);
    }
  }
}