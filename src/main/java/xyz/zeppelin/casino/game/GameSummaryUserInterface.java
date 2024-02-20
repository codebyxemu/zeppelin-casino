package xyz.zeppelin.casino.game;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.ui.CasinoUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class GameSummaryUserInterface extends InventoryUserInterface {

    private final String gameName;
    private final PlayerBetManager betManager;
    private final Consumer<PlayerBetManager> repeat;

    private GameSummaryUserInterface(PlayerBetManager betManager, Consumer<PlayerBetManager> repeat, String gameName, boolean isWin) {
        super(betManager.getPlugin(), gameName + " Summary", 27);
        this.gameName = gameName;
        this.betManager = betManager;
        this.repeat = repeat;
        addItems(isWin);
    }

    private void addItems(boolean isWin) {
        addMainMenu();
        addRepeat();
        String formattedBetAmount = DecimalFormat.getCurrencyInstance(Locale.US).format(betManager.getBetAmount());
        if (isWin) {
            String formattedWinning = DecimalFormat.getCurrencyInstance(Locale.US).format(betManager.calculateWinning());
            addInfo(
                    "§aYou played %s and won!".formatted(gameName),
                    List.of(
                            "§7Bet was §a%s".formatted(formattedBetAmount),
                            "§7Multiplier was §a%.2f".formatted(betManager.getMultiplier()),
                            "§7Total win is §a%s".formatted(formattedWinning)
                    )
            );
            addDecorations(Material.GREEN_STAINED_GLASS_PANE, "§aWin");
        } else {
            addInfo(
                    "§cYou played %s and lost!".formatted(gameName),
                    List.of(
                            "§7Bet was §a%s".formatted(formattedBetAmount),
                            "§7You lost §a%s".formatted(formattedBetAmount)
                    )
            );
            addDecorations(Material.RED_STAINED_GLASS_PANE, "§cLose");
        }
    }

    private void addInfo(String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        addItem(InventoryUserInterfaceItem.staticItem(itemStack), 13);
    }

    private void addMainMenu() {
        ItemStack itemStack = new ItemStack(Material.COMPASS, 1);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName("§cMain Menu");
        itemMeta.setLore(List.of("§7Return to the games menu"));
        itemStack.setItemMeta(itemMeta);
        addItem(
                InventoryUserInterfaceItem.staticItem(itemStack, event -> {
                    CasinoUserInterface.open(plugin, (Player) event.getWhoClicked());
                    return false;
                }),
                10
        );
    }

    private void addRepeat() {
        ItemStack itemStack = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName("§aRepeat");
        itemMeta.setLore(List.of("§7Play %s again with the same bet".formatted(gameName)));
        itemStack.setItemMeta(itemMeta);
        addItem(
                InventoryUserInterfaceItem.staticItem(itemStack, event -> {
                    repeat.accept(betManager);
                    return false;
                }),
                16
        );
    }

    private void addDecorations(Material material, String name) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        addItem(
                InventoryUserInterfaceItem.staticItem(itemStack),
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
        );
    }

    public static void open(PlayerBetManager betManager, Consumer<PlayerBetManager> repeat, String gameName, boolean isWin) {
        GameSummaryUserInterface userInterface = new GameSummaryUserInterface(betManager, repeat, gameName, isWin);
        userInterface.render();
        userInterface.register();
        userInterface.show(betManager.getPlayer());
    }
}
