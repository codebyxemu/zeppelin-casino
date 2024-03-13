package xyz.zeppelin.casino.ui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.Game;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.message.MessageList;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GamePreferencesUserInterface extends InventoryUserInterface {

    private final GamePreferencesPreset preset;
    private final Player player;
    private final EconomyBridge economyBridge;
    private final MessagesConfig messagesConfig;
    private Game.Difficulty difficulty;
    private BigDecimal betAmount;

    private GamePreferencesUserInterface(Plugin plugin, Player player, GamePreferencesPreset preset) {
        super(plugin, "Configure your Bet", 45);
        this.messagesConfig = ComponentManager.getComponentManager(plugin).getComponent(MessagesConfig.class);
        this.economyBridge = ComponentManager.getComponentManager(plugin).getComponent(EconomyBridge.class);
        this.difficulty = preset.defaultDifficulty;
        this.betAmount = preset.defaultBetAmount;
        this.player = player;
        this.preset = preset;
        addItems();
    }

    private void addItems() {
        addBorder();
        addInfo();
        addBack();
        addSetBet();
        addPlay();
        if (difficulty != null) {
            addSetDifficulties();
        }
    }

    private void addPlay() {
        addItem(
                44,
                new InventoryUserInterfaceItem() {
                    @Override
                    public ItemStack render() {
                        if (betAmount != null) {
                            ItemStack playItem = new ItemStack(Material.EMERALD_BLOCK, 1);
                            ItemMeta playMeta = Objects.requireNonNull(playItem.getItemMeta());
                            playMeta.setDisplayName(new Message("&b&lPlay").colorize().getMessage());
                            playItem.setItemMeta(playMeta);
                            return playItem;
                        } else {
                            ItemStack blockItem = new ItemStack(Material.REDSTONE_BLOCK, 1);
                            ItemMeta blockMeta = Objects.requireNonNull(blockItem.getItemMeta());
                            blockMeta.setDisplayName(new Message("&c&lPlay").colorize().getMessage());
                            blockMeta.setLore(new MessageList(List.of("&7You must set a bet amount to play.")).colorize().getMessages());
                            blockItem.setItemMeta(blockMeta);
                            return blockItem;
                        }
                    }

                    @Override
                    public boolean onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        if (betAmount == null) return false;
                        close();
                        preset.consumer.accept(betAmount, difficulty);
                        return true;
                    }
                }
        );
    }

    private void addSetBet() {
        InventoryUserInterfaceItem setBet = new InventoryUserInterfaceItem() {
            @Override
            public ItemStack render() {
                ItemStack setBetItem = new ItemStack(Material.DIAMOND, 1);
                ItemMeta setBetMeta = Objects.requireNonNull(setBetItem.getItemMeta());
                setBetMeta.setDisplayName(new Message("&b&lSet Bet Amount").colorize().getMessage());
                String formattedBetAmount;
                if (betAmount != null) {
                    formattedBetAmount = DecimalFormat.getCurrencyInstance(Locale.US).format(betAmount);
                } else {
                    formattedBetAmount = new Message("&7Not set").colorize().getMessage();
                }
                setBetMeta.setLore(new MessageList(List.of("§7Current: §f" + formattedBetAmount)).colorize().getMessages());
                setBetItem.setItemMeta(setBetMeta);
                return setBetItem;
            }

            @Override
            public boolean onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                close();
                player.sendMessage(messagesConfig.getEnterBetAmount());
                TextInputUserInterface.open(plugin, player, (input) -> {
                    BigDecimal parsed;
                    try {
                        parsed = new BigDecimal(input);
                    } catch (Exception e) {
                        return messagesConfig.getInvalidNumber();
                    }

                    String errorMessage = preset.betAmountValidator.apply(new BigDecimal(input));
                    if (errorMessage != null) return errorMessage;

                    if (!economyBridge.hasSufficientBalance(player, parsed)) {
                        return messagesConfig.getInsufficientBalanceToBet();
                    }

                    betAmount = parsed;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        GamePreferencesUserInterface.this.register();
                        GamePreferencesUserInterface.this.render();
                        GamePreferencesUserInterface.this.show(player);
                    });
                    return null;
                });
                return false;
            }
        };
        addItem(13, setBet);
    }

    private void addSetDifficulties() {
        addItem(
                31,
                createSetDifficultyItem(
                        Game.Difficulty.EASY,
                        Material.LIME_STAINED_GLASS_PANE,
                        new Message("&e&lEASY").colorize().getMessage(),
                        new MessageList(List.of("&7For the low ballers that want to earn some small cash.")).colorize().getMessages()
                )
        );
        addItem(
                32,
                createSetDifficultyItem(
                        Game.Difficulty.NORMAL,
                        Material.YELLOW_STAINED_GLASS_PANE,
                        new Message("&e&lNORMAL").colorize().getMessage(),
                        new MessageList(List.of("&7For the average players that want to earn some cash.")).colorize().getMessages()
                )
        );
        addItem(
                33,
                createSetDifficultyItem(
                        Game.Difficulty.HARD,
                        Material.RED_STAINED_GLASS_PANE,
                        new Message("&c&lHARD").colorize().getMessage(),
                        new MessageList(List.of("§7For the high rollers that want to earn some big cash.")).colorize().getMessages()
                )
        );
    }

    private InventoryUserInterfaceItem createSetDifficultyItem(
            Game.Difficulty itemDifficulty,
            Material material,
            String displayName,
            List<String> lore
    ) {
        return new InventoryUserInterfaceItem() {
            @Override
            public ItemStack render() {
                ItemStack item = new ItemStack(material, 1);
                ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                meta.setDisplayName(displayName);
                meta.setLore(lore);
                if (itemDifficulty == difficulty) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public boolean onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                Game.Difficulty previous = difficulty;
                difficulty = itemDifficulty;
                return previous != difficulty;
            }
        };
    }

    private void addBack() {
        ItemStack backItem = new ItemStack(Material.ARROW, 1);
        ItemMeta backMeta = Objects.requireNonNull(backItem.getItemMeta());
        backMeta.setDisplayName(new Message("&c&lBACK").colorize().getMessage());
        backItem.setItemMeta(backMeta);
        InventoryUserInterfaceItem back = InventoryUserInterfaceItem.staticItem(backItem, event -> {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                CasinoUserInterface.open(plugin, (Player) event.getWhoClicked());
            });
            return false;
        });
        addItem(28, back);
    }

    private void addInfo() {
        ItemStack infoItem = new ItemStack(Material.PAPER, 1);
        ItemMeta infoMeta = Objects.requireNonNull(infoItem.getItemMeta());
        infoMeta.setDisplayName(new Message("&6&lInformation").colorize().getMessage());
        infoMeta.setLore(new MessageList(List.of("&7Here you can change your game preferences.")).colorize().getMessages());
        infoItem.setItemMeta(infoMeta);
        InventoryUserInterfaceItem info = InventoryUserInterfaceItem.staticItem(infoItem);
        addItem(10, info);
    }

    private void addBorder() {
        ItemStack borderItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta borderMeta = Objects.requireNonNull(borderItem.getItemMeta());
        borderMeta.setDisplayName(" ");
        borderItem.setItemMeta(borderMeta);
        InventoryUserInterfaceItem border = InventoryUserInterfaceItem.staticItem(borderItem);
        int[] slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 17, 18, 20, 26, 27, 29, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : slots) {
            addItem(slot, border);
        }
    }

    /**
     * Represents a preset of game preferences.
     *
     * @param defaultDifficulty   Default difficulty. Null if the difficulty should not be set.
     * @param defaultBetAmount    Default bet amount.
     * @param betAmountValidator  Validator for the bet amount. Must return a message if the bet amount is invalid, or null if it is valid.
     *                            Null if the bet amount should not be validated.
     * @param difficultyValidator Validator for the difficulty. Must return a message if the difficulty is invalid, or null if it is valid.
     *                            Null if the difficulty should not be validated.
     * @param consumer            Consumer to call when the preferences are set.
     */
    public record GamePreferencesPreset(
            Game.Difficulty defaultDifficulty,
            BigDecimal defaultBetAmount,
            Function<BigDecimal, String> betAmountValidator,
            Function<Game.Difficulty, String> difficultyValidator,
            BiConsumer<BigDecimal, Game.Difficulty> consumer
    ) {
    }

    public static void open(Plugin plugin, Player player, GamePreferencesPreset preset) {
        InventoryUserInterface userInterface = new GamePreferencesUserInterface(plugin, player, preset);
        userInterface.register();
        userInterface.render();
        userInterface.show(player);
    }
}
