package xyz.zeppelin.casino.bridge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.component.PluginComponent;
import xyz.zeppelin.casino.config.MainConfig;

import java.math.BigDecimal;

public interface EconomyBridge extends PluginComponent {

    boolean withdraw(Player player, BigDecimal amount);

    boolean deposit(Player player, BigDecimal amount);

    boolean hasSufficientBalance(Player player, BigDecimal amount);

    BigDecimal getBalance(Player player);

    static EconomyBridge createDetected(Plugin plugin) {

        EconomyBridge vault = VaultEconomyBridge.detect(plugin);
        EconomyBridge playerPoints = PlayerPointsEconomyBridge.detect(plugin);

            if (vault != null) {
                plugin.getLogger().info("Detected Vault economy bridge, using Vault for economy operations.");
                return vault;
            } else if (playerPoints != null) {
                plugin.getLogger().info("Detected PlayerPoints economy bridge, using PlayerPoints for economy operations.");
                return playerPoints;
            } else {
                plugin.getLogger().info("No economy bridge detected, using in-memory economy emulation.");
                Bukkit.getLogger().info("WARNING: Vault is recommended for economy operations.");
                return new InMemoryEconomyBridge();
            }
    }
}