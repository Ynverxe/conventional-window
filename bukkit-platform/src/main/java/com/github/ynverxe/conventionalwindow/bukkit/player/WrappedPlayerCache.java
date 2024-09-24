package com.github.ynverxe.conventionalwindow.bukkit.player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class WrappedPlayerCache {

  private WrappedPlayerCache() {}

  static final WrappedPlayerCache INSTANCE = new WrappedPlayerCache();

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();
  private final Map<UUID, WrappedMinestomPlayer> wrappedPlayers = new WeakHashMap<>();

  public @NotNull WrappedMinestomPlayer compute(@NotNull Player player) {
    try {
      writeLock.lock();
      return wrappedPlayers.computeIfAbsent(player.getUniqueId(), uuid -> new WrappedMinestomPlayer(player));
    } finally {
      writeLock.unlock();
    }
  }

  public @Nullable WrappedMinestomPlayer get(@NotNull Player player) {
    try {
      readLock.lock();
      return wrappedPlayers.get(player.getUniqueId());
    } finally {
      readLock.unlock();
    }
  }
}