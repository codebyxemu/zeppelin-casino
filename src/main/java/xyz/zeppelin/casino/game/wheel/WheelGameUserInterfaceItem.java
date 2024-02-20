package xyz.zeppelin.casino.game.wheel;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.ui.GamePreferencesUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class WheelGameUserInterfaceItem implements InventoryUserInterfaceItem {

    private final Plugin plugin;
    private final MessagesConfig messagesConfig;

    public WheelGameUserInterfaceItem(Plugin plugin) {
        this.plugin = plugin;
        this.messagesConfig = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
    }

    @Override
    public ItemStack render() {
        ItemStack item = new ItemStack(Material.REDSTONE_LAMP);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName("§c§lWheel of Fortune");
        meta.setLore(List.of(
                "§7Spin the wheel to try out your fortune!",
                "",
                "§7Minimum Bet: §a$10 §7– Maximum Bet: §a$5000",
                "",
                "§eClick to play!"
        ));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GamePreferencesUserInterface.GamePreferencesPreset preset = new GamePreferencesUserInterface.GamePreferencesPreset(
                null,
                null,
                this::validateBet,
                (difficulty) -> null,
                (betAmount, difficulty) -> startGame(player, betAmount)
        );
        GamePreferencesUserInterface.open(plugin, player, preset);
        return false;
    }

    private String validateBet(BigDecimal betAmount) {
        if (betAmount.compareTo(BigDecimal.valueOf(10)) < 0) {
            return messagesConfig.getBetCanNotBeLowerThanMinimum();
        }
        if (betAmount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            return messagesConfig.getBetCanNotBeHigherThanMaximum();
        }
        return null;
    }

    private void startGame(Player player, BigDecimal betAmount) {
        PlayerBetManager betManager = new PlayerBetManager(plugin, player, betAmount);
        WheelGameSession.start(betManager);
    }
}
