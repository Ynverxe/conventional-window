package com.github.ynverxe.conventionalwindow.page;

import com.github.ynverxe.conventionalwindow.ItemRenderer;
import com.github.ynverxe.conventionalwindow.Menu;
import com.github.ynverxe.conventionalwindow.util.ItemMathUtil;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Pagination<M extends Menu<M, ?>> {

  private final @NotNull M menu;
  private final @NotNull ItemRenderer renderer;
  private final AtomicInteger page = new AtomicInteger(0);

  public Pagination(@NotNull M menu, @NotNull ItemRenderer renderer) {
    this.menu = menu;
    this.renderer = renderer;
  }

  @Contract("_ -> this")
  public Pagination<M> set(int page) {
    int current = page();

    if (current != page) {
      checkIndex(page);

      this.page.set(page);
      this.renderer.pageChange(page);
    }

    return this;
  }

  @Contract("-> this")
  public Pagination<M> first() {
    return set(0);
  }

  @Contract("-> this")
  public Pagination<M> last() {
    return set(maxPages() - 1);
  }

  @Contract("-> this")
  public Pagination<M> forward() {
    if (!hasNext()) {
      return first();
    }

    return next();
  }

  @Contract("-> this")
  public Pagination<M> back() {
    if (!hasPrevious()) {
      return last();
    }

    return previous();
  }

  @Contract("-> this")
  public Pagination<M> next() {
    return set(page() + 1);
  }

  public boolean hasNext() {
    return page() + 1 < maxPages();
  }

  @Contract("-> this")
  public Pagination<M> previous() {
    return set(page() - 1);
  }

  public boolean hasPrevious() {
    return page() > 0;
  }

  public int page() {
    return page.get();
  }

  public int maxPages() {
    return ItemMathUtil.pageCount(menu);
  }

  @NotNull
  public M menu() {
    return menu;
  }

  private void checkIndex(int index) {
    int max = maxPages();
    if (index >= max) {
      throw new IndexOutOfBoundsException(index + " >= " + max);
    }
  }
}
