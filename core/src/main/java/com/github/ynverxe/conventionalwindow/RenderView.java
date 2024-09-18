package com.github.ynverxe.conventionalwindow;

import com.github.ynverxe.conventionalwindow.item.MenuItem;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderView<I extends MenuItem<?, ?>> {

  @NotNull LinkedHashMap<Integer, I> asMap();

  @Nullable I getItem(int slot);

  default @NotNull Optional<I> optionalItem(int slot) {
    return Optional.ofNullable(getItem(slot));
  }

  int length();

}