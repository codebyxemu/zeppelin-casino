package xyz.zeppelin.casino.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.game.coinflip.CoinflipGameUserInterfaceItem;
import xyz.zeppelin.casino.game.crash.CrashGameUserInterfaceItem;
import xyz.zeppelin.casino.game.mines.MinesGameUserInterfaceItem;
import xyz.zeppelin.casino.game.slots.SlotsGameUserInterfaceItem;
import xyz.zeppelin.casino.game.wheel.WheelGameUserInterfaceItem;

import java.util.Objects;

public class CasinoUserInterface extends InventoryUserInterface {

    private CasinoUserInterface(Plugin plugin) {
        super(plugin, "Casino", 45);
        addItems();
    }

    private void addItems() {
        addCosmeticItems();
        addItem(20, new CrashGameUserInterfaceItem(plugin));
        addItem(21, new WheelGameUserInterfaceItem(plugin));
        addItem(22, new MinesGameUserInterfaceItem(plugin));
        addItem(23, new CoinflipGameUserInterfaceItem(plugin));
        addItem(24, new SlotsGameUserInterfaceItem(plugin));
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
