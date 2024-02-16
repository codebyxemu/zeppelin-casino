package xyz.zeppelin.casino.game.mines;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MinesGameDifficultyPickUserInterface extends InventoryUserInterface {

    private final Consumer<MinesGameSession.Difficulty> callback;

    private MinesGameDifficultyPickUserInterface(Plugin plugin, Consumer<MinesGameSession.Difficulty> callback) {
        super(plugin, "Pick Difficulty", 9); // ToDo: Get the title from config
        this.callback = callback;
        addItems();
    }

    private void addItems() {
        addItem(3, new DifficultyPickItem(MinesGameSession.Difficulty.EASY, getEasyItem()));
        addItem(4, new DifficultyPickItem(MinesGameSession.Difficulty.NORMAL, getNormalItem()));
        addItem(5, new DifficultyPickItem(MinesGameSession.Difficulty.HARD, getHardItem()));
    }

    private ItemStack getHardItem() {
        ItemStack hardItem = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta hardMeta = Objects.requireNonNull(hardItem.getItemMeta());
        // ToDo: Get the display name and lore from config
        hardMeta.setDisplayName("Hard");
        hardMeta.setLore(List.of(
                "More mines, higher multipliers.",
                "Higher risk, higher reward."
        ));
        hardItem.setItemMeta(hardMeta);
        return hardItem;
    }

    private ItemStack getNormalItem() {
        ItemStack normalItem = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta normalMeta = Objects.requireNonNull(normalItem.getItemMeta());
        // ToDo: Get the display name and lore from config
        normalMeta.setDisplayName("Normal");
        normalMeta.setLore(List.of(
                "Standard amount of mines and multipliers.",
                "Balanced risk and reward."
        ));
        normalItem.setItemMeta(normalMeta);
        return normalItem;
    }

    private ItemStack getEasyItem() {
        ItemStack easyItem = new ItemStack(Material.IRON_BLOCK);
        ItemMeta easyMeta = Objects.requireNonNull(easyItem.getItemMeta());
        // ToDo: Get the display name and lore from config
        easyMeta.setDisplayName("Easy");
        easyMeta.setLore(List.of(
                "Fewer mines, lower multipliers.",
                "Lower risk, lower reward."
        ));
        easyItem.setItemMeta(easyMeta);
        return easyItem;
    }

    @Override
    protected void onClose(InventoryCloseEvent event, CloseReason reason) {
        callback.accept(null);
        unregister();
    }

    @RequiredArgsConstructor
    private class DifficultyPickItem implements InventoryUserInterfaceItem {

        private final MinesGameSession.Difficulty difficulty;
        private final ItemStack renderedItem;

        @Override
        public ItemStack render() {
            return renderedItem;
        }

        @Override
        public boolean onClick(InventoryClickEvent event) {
            callback.accept(difficulty);
            event.setCancelled(true);
            return false;
        }
    }

    public static void open(Plugin plugin, Player player, Consumer<MinesGameSession.Difficulty> callback) {
        MinesGameDifficultyPickUserInterface userInterface = new MinesGameDifficultyPickUserInterface(plugin, callback);
        userInterface.render();
        userInterface.register();
        userInterface.show(player);
    }
}
