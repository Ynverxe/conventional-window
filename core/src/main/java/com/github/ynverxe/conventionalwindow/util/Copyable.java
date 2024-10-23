package com.github.ynverxe.conventionalwindow.util;

import org.jetbrains.annotations.NotNull;

public interface Copyable<T> {
  @NotNull T copy();
}