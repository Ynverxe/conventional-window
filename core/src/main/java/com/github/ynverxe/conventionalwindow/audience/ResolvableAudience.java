package com.github.ynverxe.conventionalwindow.audience;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface ResolvableAudience {

  @NotNull Audience audience();

}