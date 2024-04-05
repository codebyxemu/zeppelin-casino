package xyz.zeppelin.casino.bridge.economy;

import lombok.RequiredArgsConstructor;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.bridge.EconomyBridge;
import xyz.zeppelin.casino.component.PluginComponent;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class PlayerPointsEconomyBridge implements EconomyBridge, PluginComponent {

    private final Plugin plugin;
    private PlayerPointsAPI ppApi;

    private int has(Player player) {
        return ppApi.look(player.getUniqueId());
    }

    @Override
    public boolean withdraw(Player player, BigDecimal amount) {
        if (!(has(player) < amount.intValue())) return false;
        return ppApi.take(player.getUniqueId(), amount.intValue());
    }

    @Override
    public boolean deposit(Player player, BigDecimal amount) {
        return ppApi.give(player.getUniqueId(), amount.intValue());
    }

    @Override
    public boolean hasSufficientBalance(Player player, BigDecimal amount) {
        return has(player) >= amount.intValue();
    }

    @Override
    public BigDecimal getBalance(Player player) {
        return BigDecimal.valueOf(has(player));
    }

    @Override
    public void onEnable() {
        this.ppApi = PlayerPoints.getInstance().getAPI();
    }

    public static EconomyBridge detect(Plugin plugin) {
        try {
            Class.forName("org.black_ixx.playerpoints.PlayerPoints");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return new PlayerPointsEconomyBridge(plugin);
    }
}
