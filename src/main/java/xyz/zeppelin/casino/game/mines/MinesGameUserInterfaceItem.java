package xyz.zeppelin.casino.game.mines;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;
import xyz.zeppelin.casino.ui.TextInputUserInterface;
import xyz.zeppelin.casino.util.Utils;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
public class MinesGameUserInterfaceItem implements InventoryUserInterfaceItem {

    private final Plugin plugin;

    @Override
    public ItemStack render() {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("Mines");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        // ToDo: Get message from config
        player.sendMessage("Enter bet amount for the mines game:");
        TextInputUserInterface.open(plugin, player, input -> handleBetInput(player, input));
        event.setCancelled(true);
        player.closeInventory();
        return false;
    }

    private String handleBetInput(Player player, String input) {
        BigDecimal bet = Utils.toBigDecimalOrNull(input);
        if (bet == null) {
            return "Invalid amount. Please enter a valid number.";
        }
        EconomyBridge economyBridge = ((ZeppelinCasinoPlugin) plugin).getEconomyBridge();
        if (!economyBridge.withdraw(player, bet)) {
            return "You don't have that much money.";
        }
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            MinesGameDifficultyPickUserInterface.open(plugin, player, difficulty -> handleDifficultyPick(player, bet, difficulty));
        });
        return null;
    }

    private void handleDifficultyPick(Player player, BigDecimal bet, MinesGameSession.Difficulty difficulty) {
        boolean shouldRefund = difficulty == null;
        if (shouldRefund) {
            EconomyBridge economyBridge = ((ZeppelinCasinoPlugin) plugin).getEconomyBridge();
            economyBridge.deposit(player, bet);
            return;
        }
        MinesGameSession.start(plugin, player, bet, difficulty);
    }
}
