package com.github.ynverxe.conventionalwindow.bukkit.internal.network.packet;

import java.util.Map;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.client.play.ClientCloseWindowPacket;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import net.minestom.server.network.packet.server.play.CloseWindowPacket;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;
import net.minestom.server.network.packet.server.play.WindowPropertyPacket;
import org.jetbrains.annotations.NotNull;

public final class AllowedPackets {

  public static final @NotNull Map<Integer, Class<? extends Play>> SERVER_TO_CLIENT = Map.of(
      0x33, OpenWindowPacket.class,
      0x12, CloseWindowPacket.class,
      0x13, WindowItemsPacket.class,
      0X14, WindowPropertyPacket.class,
      0x15, SetSlotPacket.class
  );

  public static final @NotNull Map<Integer, Class<? extends ClientPacket>> CLIENT_TO_SERVER = Map.of(
      0x0E, ClientClickWindowPacket.class,
      0x0F, ClientCloseWindowPacket.class
  );

  private AllowedPackets() {}
}