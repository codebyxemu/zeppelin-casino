package xyz.zeppelin.casino.ui;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import xyz.zeppelin.casino.common.Environment;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a user interface backed by an inventory.
 */
public abstract class InventoryUserInterface implements InventoryHolder, Listener {


    private final Integer inventorySize;
    private final Map<Integer, InventoryUserInterfaceItem> items = new HashMap<>();
    private BukkitTask watcherTask;
    protected final Plugin plugin;
    protected boolean disablePlayerInventory = true;
    protected boolean autoUnregister = true;
    protected boolean disableDrag = true;
    protected Map<UUID, CloseReason> closeReasons = new HashMap<>();
    protected Set<Player> viewers = new HashSet<>();
    @Getter
    private Inventory inventory;

    public InventoryUserInterface(Plugin plugin, String title, int size) {
        Preconditions.checkArgument(size % 9 == 0, "Size must be a multiple of 9");
        Preconditions.checkArgument(size > 0, "Size must be positive");
        Preconditions.checkArgument(size <= 54, "Size must be less than or equal to 54");
        this.plugin = plugin;
        this.inventorySize = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (Environment.isDevelopmentMode()) {
            plugin.getLogger().info("Registered user interface " + this + ", starting watcher");
            startWatcher();
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        if (Environment.isDevelopmentMode()) {
            plugin.getLogger().info("Unregistered user interface " + this + ", stopping watcher");
            stopWatcher();
        }
    }

    public void show(Player player) {
        player.openInventory(inventory);
        viewers.add(player);
    }

    public void close() {
        viewers.forEach(this::close);
    }

    public void close(Player player) {
        if (!viewers.contains(player)) return;
        closeReasons.put(player.getUniqueId(), CloseReason.Plugin);
        player.closeInventory();
        closeReasons.remove(player.getUniqueId());
        viewers.remove(player);
    }

    protected final void updateTitle(String title) {
        inventory = Bukkit.createInventory(this, inventorySize, title);
        for (Player viewer : viewers) {
            closeReasons.put(viewer.getUniqueId(), CloseReason.Update);
            viewer.openInventory(inventory);
            closeReasons.remove(viewer.getUniqueId());
        }
        render();
    }

    public final void addItem(InventoryUserInterfaceItem item, int... slots) {
        for (int slot : slots) {
            addItem(slot, item);
        }
    }

    public final void addItem(int slot, InventoryUserInterfaceItem item) {
        items.put(slot, item);
    }

    public void render() {
        items.forEach((slot, item) -> inventory.setItem(slot, item.render()));
    }

    protected void clear() {
        inventory.clear();
    }

    @EventHandler
    public final void onInventoryDrag(InventoryDragEvent event) {
        if (isNotThis(event.getInventory())) return;
        if (disableDrag) event.setCancelled(true);
    }

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event) {
        if (!viewers.contains(event.getPlayer())) return;
        closeReasons.put(event.getPlayer().getUniqueId(), CloseReason.Server);
    }

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent event) {
        if (isNotThis(event.getInventory())) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (disablePlayerInventory && clickedInventory.getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }
        if (isThis(event.getClickedInventory())) {
            InventoryUserInterfaceItem item = items.get(event.getSlot());
            if (item != null) {
                if (item.onClick(event)) render();
            }
        }
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (isNotThis(event.getInventory())) return;
        Player player = (Player) event.getPlayer();
        CloseReason reason = closeReasons.getOrDefault(player.getUniqueId(), CloseReason.Player);
        if (reason == CloseReason.Update) return;
        boolean isAllowedToClose = onClose(event, reason);
        if (isAllowedToClose) {
            viewers.remove(player);
            closeReasons.remove(player.getUniqueId());
            if (viewers.isEmpty() && autoUnregister) {
                unregister();
                if (Environment.isDevelopmentMode()) {
                    plugin.getLogger().info("Unregistered " + this + " because no viewers are left");
                }
            }
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> show(player));
        }
    }

    private void startWatcher() {
        AtomicInteger noViewersTicks = new AtomicInteger();
        watcherTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (viewers.isEmpty()) {
                noViewersTicks.addAndGet(1);
            } else {
                noViewersTicks.set(0);
            }
            if (noViewersTicks.get() > 0 && noViewersTicks.get() % 1200 == 0) {
                int seconds = noViewersTicks.get() / 60;
                plugin.getLogger().warning("User interface " + this + " has been open for " + seconds + " seconds without any viewers, possibly a memory leak!");
            }
        }, 0, 1);
    }

    private void stopWatcher() {
        if (watcherTask != null) watcherTask.cancel();
    }

    /**
     * Called when the inventory is closed.
     *
     * @param event  the event
     * @param reason the reason
     * @return true if the inventory should be closed, false otherwise
     */
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        return true;
    }

    protected final boolean isThis(Inventory inventory) {
        return inventory.getHolder() == this;
    }

    protected final boolean isNotThis(Inventory inventory) {
        return inventory.getHolder() != this;
    }

    public enum CloseReason {Plugin, Player, Server, Update}
}
