package xyz.zeppelin.casino.game.mines;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MinesGameUserInterface extends InventoryUserInterface {

    private final MinesGameSession session;

    public MinesGameUserInterface(MinesGameSession session) {
        super(session.getPlugin(), formatTitle(session), 54);
        this.session = session;
        addItems();
    }

    private void addItems() {
        addGameFields();
        addCosmetic();
        addWithdraw();
        addBalance();
        addBet();
    }

    private void addBet() {
        ItemStack betItem = new ItemStack(Material.PAPER, 1);
        ItemMeta betItemMeta = Objects.requireNonNull(betItem.getItemMeta());
        String formattedBet = DecimalFormat.getCurrencyInstance(Locale.US).format(session.getBetManager().getBetAmount());
        betItemMeta.setDisplayName("§a" + formattedBet);
        betItemMeta.setLore(List.of("§7Total Bet"));
        betItem.setItemMeta(betItemMeta);
        InventoryUserInterfaceItem betUserInterfaceItem = InventoryUserInterfaceItem.staticItem(betItem);
        addItem(betUserInterfaceItem, 19);
    }

    private void addBalance() {
        ItemStack balanceItem = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta balanceItemMeta = Objects.requireNonNull(balanceItem.getItemMeta());
        String formattedBalance = DecimalFormat.getCurrencyInstance(Locale.US).format(session.getBetManager().getBalance());
        balanceItemMeta.setDisplayName("§a" + formattedBalance);
        balanceItemMeta.setLore(List.of("§7Current Balance"));
        balanceItem.setItemMeta(balanceItemMeta);
        InventoryUserInterfaceItem balanceUserInterfaceItem = InventoryUserInterfaceItem.staticItem(balanceItem);
        addItem(balanceUserInterfaceItem, 10);
    }

    private void addWithdraw() {
        addItem(
                37,
                new InventoryUserInterfaceItem() {
                    @Override
                    public ItemStack render() {
                        BigDecimal multiplier = session.getBetManager().getMultiplier();
                        String formattedMultiplier = new DecimalFormat("0.00").format(multiplier);
                        ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
                        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                        meta.setDisplayName("§aCASH OUT");
                        meta.setLore(List.of(
                                "§7You can only cash out when you are over 1.00x in multiplier.",
                                "",
                                "§7Current Profit: " + (multiplier.compareTo(BigDecimal.ZERO) > 0 ? "§a" + formattedMultiplier : "§c" + formattedMultiplier) + "x"
                        ));
                        item.setItemMeta(meta);
                        return item;
                    }

                    @Override
                    public boolean onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        BigDecimal multiplier = session.getBetManager().getMultiplier();
                        if (multiplier.compareTo(BigDecimal.ZERO) > 0) {
                            session.end();
                        }
                        return false;
                    }
                }
        );
    }

    private void addCosmetic() {
        ItemStack whiteGlassPane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta whiteGlassPaneMeta = Objects.requireNonNull(whiteGlassPane.getItemMeta());
        whiteGlassPaneMeta.setDisplayName(" ");
        whiteGlassPane.setItemMeta(whiteGlassPaneMeta);
        InventoryUserInterfaceItem whiteGlassPaneItem = InventoryUserInterfaceItem.staticItem(whiteGlassPane);
        addItem(whiteGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 16, 17, 18, 20, 25, 26, 27, 28, 29, 34, 35, 36, 38, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
    }

    private void addGameFields() {
        int[][] slotMap = new int[][]{
                {12, 13, 14, 15},
                {21, 22, 23, 24},
                {30, 31, 32, 33},
                {39, 40, 41, 42}
        };
        for (int x = 0; x < slotMap.length; x++) {
            for (int y = 0; y < slotMap[x].length; y++) {
                addItem(slotMap[x][y], new GameFieldUserInterfaceItem(session, x, y));
            }
        }
        render();
    }

    void updateTitle() {
        updateTitle(formatTitle(session));
    }

    @Override
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return true;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> session.getController().processClose());
        return true;
    }

    private static String formatTitle(MinesGameSession session) {
        BigDecimal winning = session.getBetManager().calculateWinning();
        String formattedWinning = DecimalFormat.getCurrencyInstance(Locale.US).format(winning);
        return "MINES – " + formattedWinning;
    }
}
