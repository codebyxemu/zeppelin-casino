package xyz.zeppelin.casino.ui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

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

    /**
     * Creates an implementation of InventoryUserInterfaceItem that renders a static item.
     * The item is not clickable.
     * For a clickable item, use {@link #staticItem(ItemStack, Function) staticItem}.
     *
     * @param item the item to render.
     * @return the created item.
     */
    static InventoryUserInterfaceItem staticItem(ItemStack item) {
        return staticItem(item, (event) -> {
            event.setCancelled(true);
            return false;
        });
    }

    /**
     * Creates an implementation of InventoryUserInterfaceItem that renders a static item.
     *
     * @param item          the item to render.
     * @param eventConsumer the consumer to call when the item is clicked.
     * @return the created item.
     */
    static InventoryUserInterfaceItem staticItem(ItemStack item, Function<InventoryClickEvent, Boolean> eventConsumer) {
        return new InventoryUserInterfaceItem() {
            @Override
            public ItemStack render() {
                return item;
            }

            @Override
            public boolean onClick(InventoryClickEvent event) {
                event.setCancelled(true);
                return eventConsumer.apply(event);
            }
        };
    }
}
