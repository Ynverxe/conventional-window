package com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet;

import com.github.ynverxe.conventionalwindow.bukkit.nms.common.MinestomPacketHolder;
import com.github.ynverxe.conventionalwindow.bukkit.nms.common.NMSModule;
import com.github.ynverxe.conventionalwindow.bukkit.player.WrappedMinestomPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NioBufferExtractor;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import net.minestom.server.network.packet.server.play.CloseWindowPacket;
import org.jetbrains.annotations.NotNull;

public class CustomPacketEncoder extends MessageToByteEncoder<Object> {

  private final MessageToByteEncoder<?> vanillaDelegate;
  private final WrappedMinestomPlayer player;

  public CustomPacketEncoder(@NotNull MessageToByteEncoder<?> vanillaDelegate,
      @NotNull WrappedMinestomPlayer player) {
    this.vanillaDelegate = Objects.requireNonNull(vanillaDelegate, "vanillaDelegate");
    this.player = Objects.requireNonNull(player, "player");
  }

  @Override
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return true;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {

    if (msg instanceof MinestomPacketHolder) {
      // When an inventory is closed silently, we use
      // Player#closeInventory() to maintain the minestom close logic
      // so we need to ignore the close packet to avoid the bukkit inventory being closed
      if (((MinestomPacketHolder) msg).minestomPacket() instanceof CloseWindowPacket && !player.sendClosePacket) {
        player.sendClosePacket = true; // reset
        return;
      }

      super.write(ctx, msg, promise);
    } else {
      int id = NMSModule.instance().packetId(msg);

      // Detect when a bukkit inventory is open and close the minestom inventory silently
      if (id == 0x33) { // Open Screen packet
        if (player.hasBukkitInventoryOpen()) {
          player.closeInventorySilently();
        }
      }

      vanillaDelegate.write(ctx, msg, promise);
    }
  }

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf)
      throws Exception {

    ServerPacket realPacket = ((MinestomPacketHolder) msg).minestomPacket();
    if (realPacket instanceof Play) {
      int packetId = realPacket.getId(ConnectionState.PLAY);

      NetworkBuffer buffer = new NetworkBuffer();
      buffer.write(NetworkBuffer.VAR_INT, packetId);
      buffer.write(realPacket);

      ByteBuffer nioBuffer = NioBufferExtractor.from(buffer)
          .slice(0, buffer.readableBytes());

      byteBuf.writeBytes(nioBuffer);
    }
  }
}
