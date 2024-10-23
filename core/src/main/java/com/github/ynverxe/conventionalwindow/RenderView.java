package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.MenuItem;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderView {

  @NotNull LinkedHashMap<Integer, MenuItem<?>> asMap();

  @Nullable MenuItem<?> getItem(int slot);

  default @NotNull Optional<MenuItem<?>> optionalItem(int slot) {
    return Optional.ofNullable(getItem(slot));
  }

  int length();

}