package com.github.ynverxe.conventionalwindow.bukkit.internal.nms.v1_20_5;

import com.github.ynverxe.conventionalwindow.bukkit.nms.common.MinestomPacketHolder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacket.Play;

@SuppressWarnings("rawtypes")
public class PacketWrapper implements Packet, MinestomPacketHolder {

  private final Play minestomPacket;

  public PacketWrapper(Play minestomPacket) {
    this.minestomPacket = minestomPacket;
  }

  @Override
  public PacketType<? extends Packet<?>> type() {
    return null;
  }

  @Override
  public void handle(PacketListener listener) {

  }

  @Override
  public ServerPacket minestomPacket() {
    return minestomPacket;
  }
}