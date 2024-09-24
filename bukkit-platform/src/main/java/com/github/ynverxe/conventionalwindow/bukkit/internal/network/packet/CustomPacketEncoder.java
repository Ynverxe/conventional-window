package com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet;

import com.github.ynverxe.conventionalwindow.bukkit.nms.common.MinestomPacketHolder;
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

public class CustomPacketEncoder extends MessageToByteEncoder<Object> {

  private final MessageToByteEncoder<?> vanillaDelegate;

  public CustomPacketEncoder(MessageToByteEncoder<?> vanillaDelegate) {
    this.vanillaDelegate = Objects.requireNonNull(vanillaDelegate, "vanillaDelegate");
  }

  @Override
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return true;
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    if (msg instanceof MinestomPacketHolder) {
      super.write(ctx, msg, promise);
    } else {
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
