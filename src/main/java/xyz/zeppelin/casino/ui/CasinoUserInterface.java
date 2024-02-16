package xyz.zeppelin.casino.ui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.game.mines.MinesGameUserInterfaceItem;

public class CasinoUserInterface extends InventoryUserInterface {

    private CasinoUserInterface(Plugin plugin) {
        super(plugin, "Casino", 9); // ToDo: Get the title format from config
        addItems();
    }

    private void addItems() {
        addItem(3, new MinesGameUserInterfaceItem(plugin));
    }

    public static void open(Plugin plugin, Player player) {
        CasinoUserInterface userInterface = new CasinoUserInterface(plugin);
        userInterface.render();
        userInterface.register();
        userInterface.show(player);
    }
}
