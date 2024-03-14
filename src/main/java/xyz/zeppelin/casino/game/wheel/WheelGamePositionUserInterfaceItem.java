package xyz.zeppelin.casino.game.wheel;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
public class WheelGamePositionUserInterfaceItem implements InventoryUserInterfaceItem {

    private final WheelGameSession session;
    private final int offset;

    @Override
    public ItemStack render() {
        BigDecimal multiplier = session.getGame().getOffsetPosition(offset);
        ItemStack position = new ItemStack(getMaterial(multiplier), 1);
        ItemMeta meta = Objects.requireNonNull(position.getItemMeta());
        if (multiplier.equals(BigDecimal.ZERO)) {
            meta.setDisplayName(new Message("&cLose").colorize().getMessage());
        } else {
            meta.setDisplayName(new Message("&e" + multiplier + "x").colorize().getMessage());
        }
        position.setItemMeta(meta);
        return position;
    }

    private Material getMaterial(BigDecimal multiplier) {
        if (multiplier.equals(BigDecimal.ZERO)) return Material.BARRIER;
        if (multiplier.compareTo(MATERIAL_EMERALD_MULTIPLIER) >= 0) return Material.EMERALD;
        if (multiplier.compareTo(MATERIAL_DIAMOND_MULTIPLIER) >= 0) return Material.DIAMOND;
        if (multiplier.compareTo(MATERIAL_GOLD_MULTIPLIER) >= 0) return Material.GOLD_INGOT;
        if (multiplier.compareTo(MATERIAL_IRON_MULTIPLIER) >= 0) return Material.IRON_INGOT;
        return Material.COAL;
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        return false;
    }

    private static final BigDecimal MATERIAL_IRON_MULTIPLIER = new BigDecimal("0.5");
    private static final BigDecimal MATERIAL_GOLD_MULTIPLIER = new BigDecimal("1");
    private static final BigDecimal MATERIAL_DIAMOND_MULTIPLIER = new BigDecimal("5");
    private static final BigDecimal MATERIAL_EMERALD_MULTIPLIER = new BigDecimal("10");
}
