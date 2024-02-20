package xyz.zeppelin.casino.game.crash;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class CrashGameUserInterface extends InventoryUserInterface {

    private final CrashGameSession session;

    public CrashGameUserInterface(CrashGameSession session) {
        super(session.getPlugin(), "Crash", 45);
        this.session = session;
        addItems();
    }

    private void addItems() {
        addBorder();
        addControl();
    }

    private void addControl() {
        addItem(
                new InventoryUserInterfaceItem() {
                    @Override
                    public ItemStack render() {
                        CrashGame game = session.getGame();
                        if (game.hasStarted()) {
                            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                            ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                            meta.setDisplayName("§a§lx%.2f".formatted(game.getMultiplier()));
                            meta.setLore(List.of("§a§lClick to cash out!"));
                            item.setItemMeta(meta);
                            return item;
                        } else {
                            ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                            ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                            meta.setDisplayName("§a§lStart");
                            meta.setLore(List.of("§a§lClick to start the game!"));
                            item.setItemMeta(meta);
                            return item;
                        }
                    }

                    @Override
                    public boolean onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        CrashGame game = session.getGame();
                        if (game.hasStarted()) {
                            session.getController().processStop();
                        } else {
                            session.getController().processStart();
                        }
                        return true;
                    }
                },
                22
        );
    }

    private void addBorder() {
        addItem(
                () -> {
                    ItemStack border = new ItemStack(getBorderMaterial(), 1);
                    ItemMeta borderMeta = Objects.requireNonNull(border.getItemMeta());
                    borderMeta.setDisplayName(" ");
                    border.setItemMeta(borderMeta);
                    return border;
                },
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
        );
    }

    @Override
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return true;
        session.getController().processClose();
        return true;
    }

    private Material getBorderMaterial() {
        BigDecimal multiplier = session.getGame().getMultiplier();
        if (multiplier.compareTo(BORDER_DIAMOND_MULTIPLIER) >= 0) return Material.DIAMOND_BLOCK;
        if (multiplier.compareTo(BORDER_EMERALD_MULTIPLIER) >= 0) return Material.EMERALD_BLOCK;
        if (multiplier.compareTo(BORDER_GOLD_MULTIPLIER) >= 0) return Material.GOLD_BLOCK;
        if (multiplier.compareTo(BORDER_GREEN_MULTIPLIER) >= 0) return Material.LIME_STAINED_GLASS_PANE;
        if (multiplier.compareTo(BORDER_ORANGE_MULTIPLIER) >= 0) return Material.ORANGE_STAINED_GLASS_PANE;
        if (multiplier.compareTo(BORDER_YELLOW_MULTIPLIER) >= 0) return Material.YELLOW_STAINED_GLASS_PANE;
        if (multiplier.compareTo(BORDER_RED_MULTIPLIER) >= 0) return Material.RED_STAINED_GLASS_PANE;
        return Material.WHITE_STAINED_GLASS_PANE;
    }

    private static final BigDecimal BORDER_RED_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal BORDER_YELLOW_MULTIPLIER = new BigDecimal("3.5");
    private static final BigDecimal BORDER_ORANGE_MULTIPLIER = new BigDecimal("5");
    private static final BigDecimal BORDER_GREEN_MULTIPLIER = new BigDecimal("10");
    private static final BigDecimal BORDER_GOLD_MULTIPLIER = new BigDecimal("20");
    private static final BigDecimal BORDER_EMERALD_MULTIPLIER = new BigDecimal("50");
    private static final BigDecimal BORDER_DIAMOND_MULTIPLIER = new BigDecimal("100");
}
