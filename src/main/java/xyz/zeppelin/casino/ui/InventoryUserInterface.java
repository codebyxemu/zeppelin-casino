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

import java.util.*;

/**
 * Represents a user interface backed by an inventory.
 */
public abstract class InventoryUserInterface implements InventoryHolder, Listener {

    private final Integer inventorySize;
    private final Map<Integer, InventoryUserInterfaceItem> items = new HashMap<>();
    protected final Plugin plugin;
    protected boolean disablePlayerInventory = true;
    protected boolean disableClose = false;
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
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
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

    public final void addItem(int slot, InventoryUserInterfaceItem item) {
        items.put(slot, item);
    }

    public final void render() {
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
        if (reason == CloseReason.Plugin && disableClose) {
            player.openInventory(inventory);
            return;
        }
        onClose(event, reason);
        viewers.remove(player);
    }

    protected void onClose(InventoryCloseEvent event, CloseReason reason) {
        // Do nothing by default for convenience
    }

    protected final boolean isThis(Inventory inventory) {
        return inventory.getHolder() == this;
    }

    protected final boolean isNotThis(Inventory inventory) {
        return inventory.getHolder() != this;
    }

    public enum CloseReason {Plugin, Player, Server, Update}
}
