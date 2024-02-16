package xyz.zeppelin.casino.bridge;

import org.bukkit.entity.Player;
import xyz.zeppelin.casino.component.PluginComponent;

import java.math.BigDecimal;

public interface EconomyBridge extends PluginComponent {

    boolean withdraw(Player player, BigDecimal amount);

    void deposit(Player player, BigDecimal amount);

    static EconomyBridge createDetected() {
        return createDummy();
    }

    static EconomyBridge createDummy() {
        return new EconomyBridge() {
            @Override
            public boolean withdraw(Player player, BigDecimal amount) {
                return true;
            }

            @Override
            public void deposit(Player player, BigDecimal amount) {
            }
        };
    }
}
