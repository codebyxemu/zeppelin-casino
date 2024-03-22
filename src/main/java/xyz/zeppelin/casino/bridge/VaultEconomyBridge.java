package xyz.zeppelin.casino.bridge;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.component.PluginComponent;
import xyz.zeppelin.casino.config.MainConfig;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
public class VaultEconomyBridge implements EconomyBridge, PluginComponent {

    private final Plugin plugin;
    private Economy economy;

    @Override
    public boolean withdraw(Player player, BigDecimal amount) {
        if (!economy.has(player, amount.doubleValue())) return false;
        return economy.withdrawPlayer(player, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean deposit(Player player, BigDecimal amount) {
        return economy.depositPlayer(player, amount.doubleValue()).transactionSuccess();
    }

    @Override
    public boolean hasSufficientBalance(Player player, BigDecimal amount) {
        return economy.has(player, amount.doubleValue());
    }

    @Override
    public BigDecimal getBalance(Player player) {
        return BigDecimal.valueOf(economy.getBalance(player));
    }

    @Override
    public void onEnable() {
        ServicesManager servicesManager = plugin.getServer().getServicesManager();
        economy = Objects.requireNonNull(servicesManager.getRegistration(Economy.class)).getProvider();
    }

    public static EconomyBridge detect(Plugin plugin) {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return new VaultEconomyBridge(plugin);
    }
}
