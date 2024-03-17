package xyz.zeppelin.casino.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.config.MessagesConfig;

import java.math.BigDecimal;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class PlayerBetManager {

    @Getter
    private final Plugin plugin;
    private final EconomyBridge economyBridge;
    @Getter
    private final Player player;
    @Getter
    private final BigDecimal betAmount;
    @Getter
    @Setter
    private BigDecimal multiplier = BigDecimal.ZERO;


    public PlayerBetManager(Plugin plugin, Player player, BigDecimal betAmount) {
        this.plugin = plugin;
        this.economyBridge = ComponentManager.getComponentManager(plugin).getComponent(EconomyBridge.class);
        this.player = player;
        this.betAmount = betAmount;

    }

    public boolean placeBet() {
        return economyBridge.withdraw(player, betAmount);
    }

    public boolean returnBet() {
        return economyBridge.deposit(player, betAmount);
    }

    public boolean giveWinning() {
        return economyBridge.deposit(player, calculateWinning());
    }

    public BigDecimal calculateWinning() {
        return betAmount.multiply(multiplier);
    }

    public boolean hasWinning() {
        return multiplier.compareTo(BigDecimal.ZERO) > 0;
    }

    public void addMultiplier(BigDecimal multiplier) {
        this.multiplier = this.multiplier.add(multiplier);
    }

    public BigDecimal getBalance() {
        return economyBridge.getBalance(player);
    }

    public void reset() {
        multiplier = BigDecimal.ZERO;
    }

}
