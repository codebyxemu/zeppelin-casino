package xyz.zeppelin.casino.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;
import xyz.zeppelin.casino.game.coinflip.CoinflipGameUserInterfaceItem;
import xyz.zeppelin.casino.game.crash.CrashGameUserInterfaceItem;
import xyz.zeppelin.casino.game.mines.MinesGameUserInterfaceItem;
import xyz.zeppelin.casino.game.slots.SlotsGameUserInterfaceItem;
import xyz.zeppelin.casino.game.wheel.WheelGameUserInterfaceItem;

import java.util.Objects;

public class CasinoUserInterface extends InventoryUserInterface {

    protected final MainConfig mainConfig;

    private CasinoUserInterface(Plugin plugin) {
        super(plugin, "Casino", 45);
        addItems();

        this.mainConfig = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);
    }

    private void addItems() {
        addCosmeticItems();

        int crashSlot = mainConfig.gameSlot("crash");
        int wheelSlot = mainConfig.gameSlot("wheel");
        int minesSlot = mainConfig.gameSlot("mines");
        int coinflipSlot = mainConfig.gameSlot("coinflip");
        int slotsSlot = mainConfig.gameSlot("slots");

        if (crashSlot == -1 || wheelSlot == -1 || minesSlot == -1 || coinflipSlot == -1 || slotsSlot == -1) {
            throw new IllegalStateException("Invalid game slot configuration. Please check your config.yml file.");
        }

        // Check if any of the games are disabled
        if (mainConfig.gameStatus("crash")) {
            addItem(crashSlot, new CrashGameUserInterfaceItem(plugin));
        }
        if (mainConfig.gameStatus("wheel")) {
            addItem(wheelSlot, new WheelGameUserInterfaceItem(plugin));
        }
        if (mainConfig.gameStatus("mines")) {
            addItem(minesSlot, new MinesGameUserInterfaceItem(plugin));
        }
        if (mainConfig.gameStatus("coinflip")) {
            addItem(coinflipSlot, new CoinflipGameUserInterfaceItem(plugin));
        }
        if (mainConfig.gameStatus("slots")) {
            addItem(slotsSlot, new SlotsGameUserInterfaceItem(plugin));
        }
    }

    private void addCosmeticItems() {
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta blackGlassMeta = Objects.requireNonNull(blackGlass.getItemMeta());
        blackGlassMeta.setDisplayName(" ");
        blackGlass.setItemMeta(blackGlassMeta);
        InventoryUserInterfaceItem blackGlassItem = InventoryUserInterfaceItem.staticItem(blackGlass);

        ItemStack blueGlass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
        ItemMeta blueGlassMeta = Objects.requireNonNull(blueGlass.getItemMeta());
        blueGlassMeta.setDisplayName(" ");
        blueGlass.setItemMeta(blueGlassMeta);
        InventoryUserInterfaceItem blueGlassItem = InventoryUserInterfaceItem.staticItem(blueGlass);

        addItem(blackGlassItem, 0, 1, 7, 8, 9, 11, 12, 13, 14, 15, 17, 27, 29, 30, 31, 32, 33, 35, 36, 37, 43, 44);
        addItem(blueGlassItem, 2, 3, 4, 5, 6, 10, 16, 18, 19, 25, 26, 28, 34, 38, 42);
    }

    public static void open(Plugin plugin, Player player) {
        CasinoUserInterface userInterface = new CasinoUserInterface(plugin);
        userInterface.render();
        userInterface.register();
        userInterface.show(player);
    }
}
