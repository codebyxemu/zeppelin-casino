package xyz.zeppelin.casino.bridge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.bridge.economy.InMemoryEconomyBridge;
import xyz.zeppelin.casino.bridge.economy.PlayerPointsEconomyBridge;
import xyz.zeppelin.casino.bridge.economy.VaultEconomyBridge;
import xyz.zeppelin.casino.component.PluginComponent;
import xyz.zeppelin.casino.data.StoredBet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface DatabaseBridge extends PluginComponent {

    int getWins(Player player);

    int getLosses(Player player);

    BigDecimal getTotalWagered(Player player);

    void addBet(StoredBet bet);

    List<StoredBet> getBets(Predicate<StoredBet> filter);

    StoredBet getBet(UUID uuid);

    List<StoredBet> allBets();

}