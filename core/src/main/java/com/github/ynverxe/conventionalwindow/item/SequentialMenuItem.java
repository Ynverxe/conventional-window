package com.github.ynverxe.conventionalwindow.item;

import com.github.ynverxe.conventionalwindow.item.context.ItemContext;
import com.github.ynverxe.conventionalwindow.util.Copyable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A MenuItem that returns a sequence of {@link ItemStack} distributed in the time.
 * This class holds a list of {@link Entry}. An entry holds an {@link ItemStack} and a {@link Duration}
 * representing the window of time that the {@link Entry#itemStack} is accessible.
 */
public class SequentialMenuItem extends AbstractMenuItem<SequentialMenuItem> {

  static final @NotNull SequentialMenuItem EMPTY = new SequentialMenuItem(Collections.emptyList(), ItemClickHandler.cancelClick());

  private final List<Entry> entries;
  /**
   * The index representing the current accessible {@link Entry}
   */
  private int index;
  /**
   * The last time that {@link SequentialMenuItem#index} was updated.
   */
  private long lastUpdatedTime = System.currentTimeMillis();

  @Internal
  public SequentialMenuItem(@NotNull List<Entry> entries, @NotNull ItemClickHandler clickHandler) {
    super(clickHandler);
    this.entries = entries;
  }

  @Override
  public @Nullable ItemStack get(@NotNull ItemContext context) {
    if (entries.isEmpty()) return null;

    long elapsedTime = System.currentTimeMillis() - lastUpdatedTime;
    Entry result = current();

    Entry nextEntry = next();
    while (true) {
      long currentDelay = nextEntry.duration.toMillis();

      if (elapsedTime >= currentDelay) {
        lastUpdatedTime = System.currentTimeMillis();
        if (++index >= entries.size()) {
          index = 0;
        }

        result = nextEntry;

        elapsedTime -= currentDelay;
        nextEntry = nextTo(nextEntry);
        continue;
      }

      break;
    }

    return result.itemStack;
  }

  @Contract("_, _ -> new")
  public SequentialMenuItem withEntry(@NotNull ItemStack itemStack, @NotNull Duration delayDuration) {
    List<Entry> newEntries = new ArrayList<>(entries.size() + 1);
    newEntries.addAll(entries);
    newEntries.add(new Entry(delayDuration, itemStack));
    return newItem(newEntries);
  }

  @Contract("_ -> new")
  public SequentialMenuItem withEntries(@NotNull Object @NotNull... pairs) {
    List<Entry> newEntries = new ArrayList<>(this.entries);

    for (int i = 0; i < pairs.length; i++) {
      Object first = pairs[i];
      Object second = pairs[++i];

      ItemStack itemStack;
      if (first instanceof ItemStack) {
        itemStack = (ItemStack) first;
      } else if (first instanceof Material) {
        itemStack = ItemStack.of((Material) first);
      } else {
        throw new IllegalArgumentException("Expected (ItemStack - Material), but found " + first);
      }

      if (second instanceof Number) { // assume what are ticks
        newEntries.add(new Entry(Duration.ofMillis(((Number) second).intValue() * 50L), itemStack));
      } else if (second instanceof Duration) {
        newEntries.add(new Entry(((Duration) second), itemStack));
      } else {
        throw new IllegalArgumentException("Second pair value isn't a valid delay duration value");
      }
    }

    return newItem(newEntries);
  }

  public @NotNull SequentialMenuItem withEntries(@NotNull Entry... entries) {
    List<Entry> newEntries = new ArrayList<>(this.entries);
    newEntries.addAll(Arrays.asList(entries));
    return newItem(newEntries);
  }

  public @NotNull SequentialMenuItem withEntries(@NotNull Collection<ItemStack> itemStacks, @NotNull Collection<Duration> durations) {
    if (itemStacks.size() != durations.size()) {
      throw new IllegalArgumentException("ItemStacks and Durations have not the same length");
    }

    List<Entry> newEntries = new ArrayList<>(this.entries);

    Iterator<ItemStack> itemStackIterator = itemStacks.iterator();
    Iterator<Duration> durationIterator = durations.iterator();

    while (itemStackIterator.hasNext()) {
      ItemStack itemStack = itemStackIterator.next();
      Duration duration = durationIterator.next();

      newEntries.add(new Entry(duration, itemStack));
    }

    return newItem(newEntries);
  }

  private Entry current() {
    return entries.get(index);
  }

  private Entry next() {
    int index = this.index + 1;
    if (index >= entries.size()) {
      return entries.get(0);
    }

    return entries.get(index);
  }

  private Entry nextTo(Entry entry) {
    int index = entries.indexOf(entry) + 1;
    if (index >= entries.size()) {
      return entries.get(0);
    }

    return entries.get(index);
  }

  @Override
  public @NotNull SequentialMenuItem copy() {
    return newItem(new ArrayList<>(entries));
  }

  @SuppressWarnings("unchecked")
  private @NotNull SequentialMenuItem newItem(@NotNull List<Entry> entries) {
    if (this.entries == entries) {
      throw new IllegalArgumentException("Cannot use the same entry collection");
    }

    ItemClickHandler clickHandler = clickHandler();
    if (clickHandler instanceof Copyable<?>) {
      clickHandler = ((Copyable<ItemClickHandler>) clickHandler).copy();
    }

    return new SequentialMenuItem(entries, clickHandler);
  }

  @Override
  protected @NotNull SequentialMenuItem create(@NotNull ItemClickHandler clickHandler) {
    return new SequentialMenuItem(new ArrayList<>(this.entries), clickHandler);
  }

  public static class Entry {
    private final Duration duration;
    private final @NotNull ItemStack itemStack;

    public Entry(Duration duration, @NotNull ItemStack itemStack) {
      this.duration = duration;
      this.itemStack = itemStack;
    }
  }
}