package xyz.zeppelin.casino.game.mines;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;

public class MinesGameFieldUserInterfaceItem implements InventoryUserInterfaceItem {

    private final MinesGameSession session;
    private final int x;
    private final int y;
    private MinesGame.GameField field;

    MinesGameFieldUserInterfaceItem(MinesGameSession session, int x, int y) {
        this.session = session;
        this.x = x;
        this.y = y;
    }

    @Override
    public ItemStack render() {
        if (field == null) return renderHidden();
        if (field.isMine()) return renderMine();
        return renderMultiplier((MinesGame.MultiplierField) field);
    }

    private ItemStack renderHidden() {
        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName("Field");
        itemMeta.setLore(List.of("Click to reveal."));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack renderMine() {
        ItemStack itemStack = new ItemStack(Material.TNT);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName("Mine");
        itemMeta.setLore(List.of("Oops, you lost!"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack renderMultiplier(MinesGame.MultiplierField field) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        itemMeta.setDisplayName("Multiplier %.2f".formatted(field.multiplier()));
        itemMeta.setLore(List.of("You won %.2fx your bet!".formatted(field.multiplier())));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        MinesGame.GameField field = session.getController().processFieldClick(x, y);
        event.setCancelled(true);
        if (field != null) {
            this.field = field;
            return true;
        } else {
            return false;
        }
    }

    private static final MathContext MATH_CONTEXT_TWO_DIGITS = new MathContext(2);
}
