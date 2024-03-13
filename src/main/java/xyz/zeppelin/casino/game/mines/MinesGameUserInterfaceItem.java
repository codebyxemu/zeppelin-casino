package xyz.zeppelin.casino.game.mines;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.Game;
import xyz.zeppelin.casino.game.PlayerBetManager;
import xyz.zeppelin.casino.ui.GamePreferencesUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MinesGameUserInterfaceItem implements InventoryUserInterfaceItem {

    private final Plugin plugin;
    private final MessagesConfig messagesConfig;
    private final BigDecimal maxBet;
    private final BigDecimal minBet;

    public MinesGameUserInterfaceItem(Plugin plugin) {
        this.plugin = plugin;
        this.messagesConfig = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
        MainConfig mainConfig = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);
        MinesGame.Config config = mainConfig.getMinesConfig();
        this.maxBet = config.maxBet();
        this.minBet = config.minBet();
    }

    @Override
    public ItemStack render() {
        String minBetFormatted = DecimalFormat.getCurrencyInstance(Locale.US).format(minBet);
        String maxBetFormatted = DecimalFormat.getCurrencyInstance(Locale.US).format(maxBet);
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName("§c§lMines");
        meta.setLore(List.of(
                "§eFeatured Game",
                "",
                "§7Classic mines game, open tiles to find a multiplier... or a mine!",
                "",
                "§7Minimum Bet: §a%s §7– Maximum Bet: §a%s".formatted(minBetFormatted, maxBetFormatted),
                "§7Difficulty: §aEasy, §eMedium, §cHard",
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
                Game.Difficulty.EASY,
                null,
                this::validateBet,
                (difficulty) -> null,
                (betAmount, difficulty) -> startGame(player, betAmount, difficulty)
        );
        GamePreferencesUserInterface.open(plugin, player, preset);
        return false;
    }

    private String validateBet(BigDecimal betAmount) {
        if (betAmount.compareTo(minBet) < 0) {
            return messagesConfig.getBetCanNotBeLowerThanMinimum();
        }
        if (betAmount.compareTo(maxBet) > 0) {
            return messagesConfig.getBetCanNotBeHigherThanMaximum();
        }
        return null;
    }

    private void startGame(Player player, BigDecimal betAmount, Game.Difficulty difficulty) {
        PlayerBetManager betManager = new PlayerBetManager(plugin, player, betAmount);
        MinesGameSession.start(betManager, difficulty);
    }

    public void quickOpen(Player player) {
        GamePreferencesUserInterface.GamePreferencesPreset preset = new GamePreferencesUserInterface.GamePreferencesPreset(
                Game.Difficulty.EASY,
                null,
                this::validateBet,
                (difficulty) -> null,
                (betAmount, difficulty) -> startGame(player, betAmount, difficulty)
        );
        GamePreferencesUserInterface.open(plugin, player, preset);
    }
}
