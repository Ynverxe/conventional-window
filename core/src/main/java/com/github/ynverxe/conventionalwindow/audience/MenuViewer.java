package com.github.ynverxe.conventionalwindow.audience;

import java.util.List;
import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MenuViewer<V> implements ForwardingAudience {

  private final @NotNull V rawViewer;
  private final @NotNull Player player;

  public MenuViewer(@NotNull V rawViewer, @NotNull Player player) {
    this.rawViewer = Objects.requireNonNull(rawViewer, "rawViewer");
    this.player = Objects.requireNonNull(player, "player");
  }

  @Override
  public @NotNull Iterable<? extends Audience> audiences() {
    if (player instanceof ResolvableAudience) {
      return List.of(((ResolvableAudience) player).audience());
    }

    return List.of(player);
  }

  public @NotNull Player player() {
    return player;
  }

  @NotNull
  public V rawViewer() {
    return rawViewer;
  }
}
