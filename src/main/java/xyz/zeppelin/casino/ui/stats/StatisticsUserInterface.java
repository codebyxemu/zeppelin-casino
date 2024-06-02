package xyz.zeppelin.casino.ui.stats;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zeppelin.casino.bridge.DatabaseBridge;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.component.PluginComponent;
import xyz.zeppelin.casino.data.StoredBet;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.ui.CasinoUserInterface;
import xyz.zeppelin.casino.utils.ItemBuilder;

import java.math.BigDecimal;

@Getter
public class StatisticsUserInterface implements Listener {

    private Player target;
    private Player executor;
    private final boolean self;

    private Inventory inventory;

    protected DatabaseBridge databaseBridge;
    protected Plugin plugin;

    public StatisticsUserInterface(Plugin plugin, Player target, Player executor) {
        this.plugin = plugin;

        this.target = target;
        this.executor = executor;
        this.self = target.equals(executor);

        this.inventory = Bukkit.createInventory(null, 27, "Statistics");
        this.databaseBridge = ComponentManager.getComponentManager(
                plugin
        ).getComponent(DatabaseBridge.class);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        renderInventory();
        executor.openInventory(inventory);
    }

    public void renderInventory() {
        // Render the inventory
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                .displayname(new Message("&a" + target.getName()).colorize().getMessage())
                .build();

        // head: 11
        // items: 13, 14, 15, 16, 17

        double winningsTotal = databaseBridge.allBets().stream().filter(StoredBet::isWin).mapToDouble(StoredBet::getTotalWinningsAsDouble).sum();
        double lossesTotal = databaseBridge.allBets().stream().filter(bet -> !bet.isWin()).mapToDouble(StoredBet::getAmountAsDouble).sum();

        // Create both formatted values as strings with a maximum of 2 decimal places, with a dollar sign.
        String winningsTotalFormatted = String.format("$%.2f", winningsTotal);
        String lossesTotalFormatted = String.format("$%.2f", lossesTotal);

        ItemStack totalWagered = new ItemBuilder(Material.DIAMOND)
                .displayname(new Message("&aTotal Wagered").colorize().getMessage())
                .lore(
                        new Message("&7Total amount of money wagered:").colorize().getMessage(),
                        new Message("&a$" + databaseBridge.getTotalWagered(target)).colorize().getMessage()
                )
                .build();

        ItemStack totalWins = new ItemBuilder(Material.DIAMOND)
                .displayname(new Message("&aTotal Winnings").colorize().getMessage())
                .lore(
                        new Message("&7Total amount of money won:").colorize().getMessage(),
                        new Message("&a" + winningsTotalFormatted).colorize().getMessage()
                )
                .build();

        ItemStack totalLosses = new ItemBuilder(Material.DIAMOND)
                .displayname(new Message("&aTotal Losses").colorize().getMessage())
                .lore(
                        new Message("&7Total of money lost:").colorize().getMessage(),
                        new Message("&a" + lossesTotalFormatted).colorize().getMessage()
                )
                .build();

        // Get the total amount of money received from the casino, that has not been lost
        BigDecimal lossesAmountOfMoney = databaseBridge.allBets().stream()
                .filter(StoredBet::isWin)
                .map(StoredBet::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        inventory.setItem(10, head);
        inventory.setItem(12, totalWagered);
        inventory.setItem(13, totalWins);
        inventory.setItem(14, totalLosses);

        inventory.setItem(26, new ItemBuilder(Material.BARRIER).displayname(new Message("&cBack").colorize().getMessage()).build());

        // Fill the rest of the inventory with glass panes
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayname(" ").build());
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory().equals(inventory)) || event.getClickedInventory() == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            CasinoUserInterface.open(plugin, executor);
        }
    }




}
