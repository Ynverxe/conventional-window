package com.github.ynverxe.conventionalwindow.bukkit.nms.common;

import net.minestom.server.network.packet.server.ServerPacket;

public interface MinestomPacketHolder {
  ServerPacket minestomPacket();
}