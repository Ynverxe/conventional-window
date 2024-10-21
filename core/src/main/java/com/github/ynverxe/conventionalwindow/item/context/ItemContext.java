package com.github.ynverxe.conventionalwindow.item.context;

import com.github.ynverxe.conventionalwindow.Menu;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Used to expose data to {@link com.github.ynverxe.conventionalwindow.item.MenuItem} related logic. This class is created every tick on {@link Menu#tick()} or when an item click event is called.
 * You can configure an ItemContext at the moment of be created using {@link Menu#configureItemContext}
 */
public class ItemContext extends LinkedHashMap<String, Object> {

  private final @NotNull Menu<?, ?> menu;

  public ItemContext(@NotNull Menu<?, ?> menu) {
    this.menu = Objects.requireNonNull(menu, "menu");
  }

  public @NotNull Menu<?, ?> menu() {
    return menu;
  }

  public <T extends Menu<?, ?>> @UnknownNullability T menuAs(@NotNull Class<T> expected) {
    if (expected.isInstance(menu)) {
      return expected.cast(menu);
    }

    return null;
  }
}