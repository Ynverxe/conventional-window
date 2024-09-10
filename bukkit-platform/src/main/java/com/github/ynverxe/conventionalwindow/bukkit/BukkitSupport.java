package com.github.ynverxe.conventionalwindow.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.inventory.InventoryItemChangeEvent;
import net.minestom.server.item.ItemStack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class BukkitSupport {

  private static volatile boolean initiated = false;

  private BukkitSupport() {}

  public static void init() {
    if (initiated) return;

    MinecraftServer minecraftServer = MinecraftServer.init();

    MinecraftServer.getGlobalEventHandler()
        .addListener(
            InventoryItemChangeEvent.class,
            event -> {
              BukkitInventoryAdapter bukkitInventoryAdapter =
                  (BukkitInventoryAdapter) event.getInventory();
              if (bukkitInventoryAdapter == null) return;

              bukkitInventoryAdapter
                  .bukkitInventory()
                  .setItem(event.getSlot(), fromMinestomItem(event.getNewItem()));
            });

    initiated = true;
  }

  public static @NotNull org.bukkit.inventory.ItemStack fromMinestomItem(ItemStack itemStack) {
    try {
      if (itemStack.isAir()) {
        return new org.bukkit.inventory.ItemStack(Material.AIR);
      }

      CompoundBinaryTag compound = itemStack.toItemNBT();

      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        BinaryTagTypes.COMPOUND.write(compound, new DataOutputStream(outputStream));

        DataInput inputStream =
            new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()));

        CompoundTag tag = CompoundTag.TYPE.load(inputStream, NbtAccounter.unlimitedHeap());

        Frozen registryAccess = net.minecraft.server.MinecraftServer.getServer().registryAccess();
        net.minecraft.world.item.ItemStack nmsItem =
            net.minecraft.world.item.ItemStack.parseOptional(registryAccess, tag);

        return nmsItem.asBukkitCopy();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
