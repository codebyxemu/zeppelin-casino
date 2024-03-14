package xyz.zeppelin.casino.game.wheel;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.zeppelin.casino.message.Message;
import xyz.zeppelin.casino.ui.InventoryUserInterface;
import xyz.zeppelin.casino.ui.InventoryUserInterfaceItem;

import java.util.Objects;

public class WheelGameUserInterface extends InventoryUserInterface {

    private final WheelGameSession session;
    private final ItemStack decorationItemStack = createDecoration();

    public WheelGameUserInterface(WheelGameSession session) {
        super(session.getPlugin(), "Wheel of Fortune", 45);
        this.session = session;
        addItems();
    }

    @Override
    public void render() {
        super.render();
        ItemStack[] contents = getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack != null) continue;
            getInventory().setItem(i, decorationItemStack);
        }
    }

    private ItemStack createDecoration() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        return item;
    }

    private void addItems() {
        addDecorations();
        addPositions();
        addStart();
    }

    private void addStart() {
        addItem(
                new InventoryUserInterfaceItem() {
                    @Override
                    public ItemStack render() {
                        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
                        if (session.getGame().hasStarted()) {
                            itemMeta.setDisplayName(new Message("&a&lSpinning...").colorize().getMessage());
                        } else {
                            itemMeta.setDisplayName(new Message("&a&lSpin").colorize().getMessage());
                        }
                        item.setItemMeta(itemMeta);
                        return item;
                    }

                    @Override
                    public boolean onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        if (!session.getGame().hasStarted()) {
                            session.getController().processStart();
                        }
                        return false;
                    }
                },
                4, 22, 31, 40
        );
    }

    private void addPositions() {
        addItem(27, new WheelGamePositionUserInterfaceItem(session, -4));
        addItem(19, new WheelGamePositionUserInterfaceItem(session, -3));
        addItem(11, new WheelGamePositionUserInterfaceItem(session, -2));
        addItem(12, new WheelGamePositionUserInterfaceItem(session, -1));
        addItem(13, new WheelGamePositionUserInterfaceItem(session, 0));
        addItem(14, new WheelGamePositionUserInterfaceItem(session, 1));
        addItem(15, new WheelGamePositionUserInterfaceItem(session, 2));
        addItem(25, new WheelGamePositionUserInterfaceItem(session, 3));
        addItem(35, new WheelGamePositionUserInterfaceItem(session, 4));
    }

    private void addDecorations() {
    }

    @Override
    protected boolean onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return true;
        if (reason == CloseReason.Player && session.getGame().hasStarted()) return false;
        session.getController().processClose();
        return true;
    }
}
