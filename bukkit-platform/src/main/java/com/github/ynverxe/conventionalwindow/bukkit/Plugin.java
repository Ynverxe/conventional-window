package com.github.ynverxe.conventionalwindow.bukkit;

import com.github.ynverxe.conventionalwindow.item.ItemProvider;
import com.github.ynverxe.conventionalwindow.slot.SlotIterator;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

  private BukkitMenu menu;

  @Override
  public void onEnable() {
    BukkitSupport.init();

    BukkitMenuContainer menuContainer = new BukkitMenuContainer(this);

    menu = menuContainer.newMenu(InventoryType.CHEST_6_ROW);

    menu.staticItemContainer()
        .fill(
            SlotIterator.sixRowsChestBorders(), ItemProvider.of(Material.BLACK_STAINED_GLASS_PANE));

    ItemProvider provider =
        ItemProvider.delayed()
            .add(
                ItemStack.of(Material.DIAMOND)
                    .withCustomName(Component.text("Hi! I'm a diamond :D", NamedTextColor.AQUA)),
                20)
            .modifyLast(
                itemStack ->
                    itemStack
                        .withCustomName(
                            Component.text("I LIED! I'm an emerald >:D", NamedTextColor.RED))
                        .withMaterial(Material.EMERALD),
                20);

    menu.pageableItemContainer().add(provider);

    menu.scheduler()
        .buildTask(
            () -> {
              int random = ThreadLocalRandom.current().nextInt();
              menu.renderTitle(Component.text("Random number: " + random));
            })
        .repeat(TaskSchedule.tick(1))
        .schedule();

    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    super.onDisable();
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    menu.render(event.getPlayer());
  }
}
