package xyz.zeppelin.casino.ui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an item in a user interface.
 */
public interface InventoryUserInterfaceItem {

    /**
     * Renders the item.
     *
     * @return the rendered item as an ItemStack.
     */
    ItemStack render();

    /**
     * Called when the item is clicked.
     *
     * @param event the click event.
     * @return whether the ui should be re-rendered.
     */
    default boolean onClick(InventoryClickEvent event) {
        // Do nothing by default for convenience
        return false;
    }
}
