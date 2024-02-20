package xyz.zeppelin.casino.game.coinflip;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.util.Objects;
import java.util.function.Supplier;

public class CoinflipGameUserInterface extends InventoryUserInterface {

    private final CoinflipGameSession session;
    private CoinflipGame.Side leftSide = CoinflipGame.Side.HEADS;
    private CoinflipGame.Side rightSide = CoinflipGame.Side.TAILS;
    private final ItemStack tails = createTails();
    private final ItemStack heads = createHeads();
    private int animationTick = 1;
    private int animationSlowdown = 5;

    public CoinflipGameUserInterface(CoinflipGameSession session) {
        super(session.getPlugin(), "Coin Flip", 45);
        this.session = session;
        addItems();
    }

    @Override
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return true;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> session.getController().processClose());
        return true;
    }

    void playAnimation() {
        if (animationTick > 60) {
            leftSide = session.getGame().getWinnerSide();
            rightSide = session.getGame().getWinnerSide();
            render();
            return;
        }
        if (animationTick % animationSlowdown == 0) {
            CoinflipGame.Side previousLeftSide = leftSide;
            leftSide = rightSide;
            rightSide = previousLeftSide;
            render();
        }
        if (animationTick % 20 == 0) {
            animationSlowdown += 5;
        }
        animationTick++;
    }

    private void addItems() {
        addDecorations();
        addLeftSide();
        addRightSide();
        addStatus();
    }

    private void addLeftSide() {
        addSide(() -> leftSide, 10, 11, 12, 19, 20, 28, 29, 30);
    }

    private void addRightSide() {
        addSide(() -> rightSide, 14, 15, 16, 24, 25, 32, 33, 34);
    }

    private void addSide(Supplier<CoinflipGame.Side> sideSupplier, int... slots) {
        addItem(
                new InventoryUserInterfaceItem() {
                    @Override
                    public ItemStack render() {
                        return sideSupplier.get() == CoinflipGame.Side.HEADS ? heads : tails;
                    }

                    @Override
                    public boolean onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        session.getController().processPickSide(sideSupplier.get());
                        return true;
                    }
                },
                slots
        );
    }

    private void addStatus() {
        addItem(
                () -> {
                    ItemStack status = getStatusItemMaterial();
                    ItemMeta statusMeta = Objects.requireNonNull(status.getItemMeta());
                    if (statusMeta instanceof SkullMeta) {
                        ((SkullMeta) statusMeta).setOwningPlayer(session.getPlayer());
                    }
                    if (animationTick > 60) {
                        statusMeta.setDisplayName(session.getGame().isWin() ? "§aYou won!" : "§cYou lost!");
                    } else if (animationTick > 1) {
                        statusMeta.setDisplayName("§aFlipping the coin...");
                    } else {
                        statusMeta.setDisplayName("§aPick a side to flip the coin.");
                    }
                    status.setItemMeta(statusMeta);
                    return status;
                },
                22
        );
    }

    private ItemStack getStatusItemMaterial() {
        Material material;
        if (animationTick > 60) {
            material = session.getGame().isWin() ? Material.PLAYER_HEAD : Material.BARRIER;
        } else if (animationTick == 1) {
            material = Material.PLAYER_HEAD;
        } else {
            material = leftSide == CoinflipGame.Side.HEADS ? Material.PLAYER_HEAD : Material.BARRIER;
        }
        return new ItemStack(material);
    }

    private ItemStack createHeads() {
        ItemStack heads = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta headsMeta = Objects.requireNonNull(heads.getItemMeta());
        headsMeta.setDisplayName("§9Heads");
        heads.setItemMeta(headsMeta);
        return heads;
    }

    private ItemStack createTails() {
        ItemStack tails = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta tailsMeta = Objects.requireNonNull(tails.getItemMeta());
        tailsMeta.setDisplayName("§eTails");
        tails.setItemMeta(tailsMeta);
        return tails;
    }

    private void addDecorations() {
        ItemStack whiteGlassPane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta whiteGlassPaneMeta = Objects.requireNonNull(whiteGlassPane.getItemMeta());
        whiteGlassPaneMeta.setDisplayName(" ");
        whiteGlassPane.setItemMeta(whiteGlassPaneMeta);
        InventoryUserInterfaceItem whiteGlassPaneItem = InventoryUserInterfaceItem.staticItem(whiteGlassPane);
        addItem(whiteGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
    }
}
