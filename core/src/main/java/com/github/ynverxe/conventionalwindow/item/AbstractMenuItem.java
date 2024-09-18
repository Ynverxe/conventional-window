package com.github.ynverxe.conventionalwindow.item;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public abstract class AbstractMenuItem<E, T extends AbstractMenuItem<E, T, I>, I extends AbstractMenuItem<?, ?, ?>> implements MenuItem<E, T> {

  private final @NotNull ItemClickHandler<E> clickHandler;

  protected AbstractMenuItem(@NotNull ItemClickHandler<E> clickHandler) {
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  @Override
  public @NotNull ItemClickHandler<E> clickHandler() {
    return clickHandler;
  }

  @Override
  public <S, I extends MenuItem<S, I>> MenuItem<S, I> withClickHandler(
      @NotNull ItemClickHandler<S> clickHandler) {
    return (MenuItem<S, I>) create(clickHandler);
  }

  @Override
  public @NotNull T copy() {
    return (T) create(clickHandler);
  }

  protected abstract <S> @NotNull I create(@NotNull ItemClickHandler<S> clickHandler);

}