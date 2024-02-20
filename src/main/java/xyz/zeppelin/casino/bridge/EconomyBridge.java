package xyz.zeppelin.casino.bridge;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.PluginComponent;

import java.math.BigDecimal;

public interface EconomyBridge extends PluginComponent {

    boolean withdraw(Player player, BigDecimal amount);

    boolean deposit(Player player, BigDecimal amount);

    boolean hasSufficientBalance(Player player, BigDecimal amount);

    BigDecimal getBalance(Player player);

    static EconomyBridge createDetected(Plugin plugin) {
        EconomyBridge vault = VaultEconomyBridge.detect(plugin);
        if (vault != null) {
            plugin.getLogger().info("Detected Vault economy bridge, using Vault for economy operations.");
            return vault;
        } else {
            plugin.getLogger().info("No economy bridge detected, using in-memory economy emulation.");
            return new InMemoryEconomyBridge();
        }
    }
}
