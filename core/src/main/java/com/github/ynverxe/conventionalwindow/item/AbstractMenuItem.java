package com.github.ynverxe.conventionalwindow.item;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMenuItem<T extends AbstractMenuItem<T>> implements MenuItem<T> {

  private final @NotNull ItemClickHandler clickHandler;

  protected AbstractMenuItem(@NotNull ItemClickHandler clickHandler) {
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  @Override
  public @NotNull ItemClickHandler clickHandler() {
    return clickHandler;
  }

  @Override
  public T withClickHandler(@NotNull ItemClickHandler clickHandler) {
    return create(clickHandler);
  }

  @Override
  public @NotNull T copy() {
    return create(clickHandler);
  }

  protected abstract @NotNull T create(@NotNull ItemClickHandler clickHandler);

}