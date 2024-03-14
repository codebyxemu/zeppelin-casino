package xyz.zeppelin.casino.game.slots;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.message.MessageList;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.util.List;
import java.util.Objects;

public class SlotsGameUserInterface extends InventoryUserInterface {

    private final SlotsGameSession session;

    public SlotsGameUserInterface(SlotsGameSession session) {
        super(session.getPlugin(), "Slots", 54);
        this.session = session;
        addItems();
    }

    private void addItems() {
        addDecorations();
        addSlots();
        addPlay();
    }

    private void addPlay() {
        InventoryUserInterfaceItem playItem = new InventoryUserInterfaceItem() {
            @Override
            public ItemStack render() {
                ItemStack play = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                ItemMeta playMeta = Objects.requireNonNull(play.getItemMeta());
                if (session.getGame().hasStarted()) {
                    playMeta.setDisplayName(new Message("&aPlaying...").colorize().getMessage());
                } else {
                    playMeta.setDisplayName(new Message("&aPlay").colorize().getMessage());
                }
                play.setItemMeta(playMeta);
                return play;
            }

            @Override
            public boolean onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                if (!session.getGame().hasStarted()) session.getController().processStart();
                return false;
            }
        };
        addItem(playItem, 39, 40, 41);
    }

    private void addSlots() {
        addItem(createSlot(1), 21);
        addItem(createSlot(2), 22);
        addItem(createSlot(3), 23);
    }

    private InventoryUserInterfaceItem createSlot(int ordinal) {
        return new InventoryUserInterfaceItem() {
            @Override
            public ItemStack render() {
                SlotsGame.Slot slot = session.getGame().getSlot(ordinal);
                ItemStack item = new ItemStack(slot.material(), 1);
                ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
                meta.setDisplayName(new Message("&ax%.2f".formatted(slot.multiplier())).colorize().getMessage());
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public boolean onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                return false;
            }
        };
    }

    private void addDecorations() {
        ItemStack whitePane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta whitePaneMeta = Objects.requireNonNull(whitePane.getItemMeta());
        whitePaneMeta.setDisplayName(new Message("&7Match three slots to win!").colorize().getMessage());
        whitePaneMeta.setLore(new MessageList(List.of(
                "&7Each slot has a different multiplier.",
                "",
                "&7To play the game, click the green button."
        )).colorize().getMessages());
        whitePane.setItemMeta(whitePaneMeta);
        InventoryUserInterfaceItem whitePaneItem = InventoryUserInterfaceItem.staticItem(whitePane);
        addItem(whitePaneItem, 11, 12, 13, 14, 15, 20, 24, 29, 30, 31, 32, 33);

        ItemStack blackPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta blackPaneMeta = Objects.requireNonNull(blackPane.getItemMeta());
        blackPaneMeta.setDisplayName(" ");
        blackPane.setItemMeta(blackPaneMeta);
        InventoryUserInterfaceItem blackPaneItem = InventoryUserInterfaceItem.staticItem(blackPane);
        addItem(blackPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
    }

    @Override
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return true;
        if (reason == CloseReason.Player && session.getGame().hasStarted()) return false;
        session.getController().processClose();
        return true;
    }
}
