package com.github.ynverxe.conventionalwindow.item;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelayedItemProvider implements ItemProvider {
  private static final Entry DEFAULT = new Entry(Duration.ofMillis(0), ItemStack.AIR);
  private final AtomicLong lastUpdatedTime = new AtomicLong(System.currentTimeMillis());
  private Entry first = DEFAULT;
  private Entry tail = first;
  private final AtomicReference<Entry> actual = new AtomicReference<>(tail);

  @Contract("_, _ -> this")
  public DelayedItemProvider add(@NotNull ItemStack itemStack, @NotNull Duration duration) {
    synchronized (tail) {
      Entry entry = new Entry(duration, itemStack);

      if (first == DEFAULT) {
        first = entry;
      }

      entry.next = first;
      this.tail.next = entry;
      this.tail = entry;
    }
    return this;
  }

  @Contract("_, _ -> this")
  public DelayedItemProvider add(@NotNull ItemStack itemStack, int ticks) {
    return add(itemStack, Duration.ofMillis(50L * ticks));
  }

  @Contract("_, _ -> this")
  public DelayedItemProvider modifyLast(@NotNull Function<ItemStack, ItemStack> modifier, @NotNull Duration duration) {
    return add(modifier.apply(tail.itemStack), duration);
  }

  @Contract("_, _ -> this")
  public DelayedItemProvider modifyLast(@NotNull Function<ItemStack, ItemStack> modifier, int ticks) {
    return modifyLast(modifier, Duration.ofMillis(50L * ticks));
  }

  @Override
  public @Nullable ItemStack get() {
    synchronized (tail) {
      long lastUpdatedTime = this.lastUpdatedTime.get();

      long elapsedTime = System.currentTimeMillis() - lastUpdatedTime;
      Entry current = this.actual.get();

      while (true) {
        Entry nextEntry = current.next;
        long currentDelay = nextEntry.delayDuration.toMillis();

        if (elapsedTime >= currentDelay) {
          current = nextEntry;

          elapsedTime -= currentDelay;

          update(current);
          continue;
        }

        break;
      }

      return current.itemStack;
    }
  }

  private void update(Entry entry) {
    this.actual.set(entry);
    updateLastUpdatedTime();
  }

  private void updateLastUpdatedTime() {
    Entry entry = this.actual.get();
    if (entry == null) return;

    lastUpdatedTime.set(System.currentTimeMillis());
  }

  static class Entry {

    private final Duration delayDuration;
    private final ItemStack itemStack;
    private Entry next;

    public Entry(Duration delayDuration, ItemStack itemStack) {
      this.delayDuration = delayDuration;
      this.itemStack = itemStack;
    }
  }
}