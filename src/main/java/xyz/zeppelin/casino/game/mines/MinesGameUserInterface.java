package xyz.zeppelin.casino.game.mines;

import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.zeppelin.casino.ui.InventoryUserInterface;

import java.text.DecimalFormat;
import java.util.Locale;

public class MinesGameUserInterface extends InventoryUserInterface {

    private final MinesGameSession session;

    public MinesGameUserInterface(MinesGameSession session, int height) {
        super(session.getPlugin(), "", height * 9); // ToDo: Get the title format from config
        this.session = session;
        updateTitle();
        addItems(session, height);
    }

    private void addItems(MinesGameSession session, int height) {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < height; y++) {
                int slot = y * 9 + x;
                addItem(slot, new GameFieldUserInterfaceItem(session, x, y));
            }
        }
        render();
    }

    void updateTitle() {
        String formattedTitle = "Mines – " + DecimalFormat.getCurrencyInstance(Locale.US).format(session.getGame().getBet()); // ToDo: Get the title format from config
        updateTitle(formattedTitle);
    }

    @Override
    protected void onClose(InventoryCloseEvent event, CloseReason reason) {
        if (reason == CloseReason.Plugin) return;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            session.getController().processClose();
        });
    }
}
