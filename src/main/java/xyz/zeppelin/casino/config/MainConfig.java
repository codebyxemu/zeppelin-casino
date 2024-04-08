package xyz.zeppelin.casino.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.game.Game;
import xyz.zeppelin.casino.game.coinflip.CoinflipGame;
import xyz.zeppelin.casino.game.crash.CrashGame;
import xyz.zeppelin.casino.game.mines.MinesGame;
import xyz.zeppelin.casino.game.slots.SlotsGame;
import xyz.zeppelin.casino.game.wheel.WheelGame;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class MainConfig extends BaseConfig {

    public MainConfig(File file, String defaultName, Logger logger) {
        super(file, defaultName, logger);
    }

    public String getServerName() {
        return Objects.requireNonNull(configuration.getString("license.server-name"));
    }

    public String getLicenseKey() {
        return Objects.requireNonNull(configuration.getString("license.license-key"));
    }

    public boolean isBetLimitationEnabled() {
        return configuration.getBoolean("gamble-responsibility.bet-limitation.enabled");
    }

    public int getBetLimit() {
        return configuration.getInt("gamble-responsibility.bet-limitation.max-bet");
    }

    public boolean gameStatus(String gameName) {
        String[] acceptedGames = {"coinflip", "crash", "mines", "slots", "wheel"};
        if (List.of(acceptedGames).contains(gameName)) {
            return configuration.getBoolean("games." + gameName);
        } else {
            return false;
        }
    }

    public int gameSlot(String gameName) {
        String[] acceptedGames = {"coinflip", "crash", "mines", "slots", "wheel"};
        if (List.of(acceptedGames).contains(gameName)) {
            return configuration.getInt("casino-gui." + gameName);
        } else {
            return -1;
        }
    }



    public CoinflipGame.Config getCoinflipConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("coinflip"));
        BigDecimal maxBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-bet")));
        BigDecimal minBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("min-bet")));
        return new CoinflipGame.Config(maxBet, minBet);
    }

    public SlotsGame.Config getSlotsConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("slots"));
        ConfigurationSection itemsSection = Objects.requireNonNull(gameSection.getConfigurationSection("items"));
        List<SlotsGame.SlotConfig> slotsConfig = itemsSection.getKeys(false).stream().map((materialName) -> {
            ConfigurationSection section = Objects.requireNonNull(itemsSection.getConfigurationSection(materialName));
            Material material = Material.matchMaterial(Objects.requireNonNull(materialName));
            BigDecimal chance = new BigDecimal(Objects.requireNonNull(section.getString("chance")));
            BigDecimal multiplier = new BigDecimal(Objects.requireNonNull(section.getString("multiplier")));
            return new SlotsGame.SlotConfig(material, chance, multiplier);
        }).toList();
        BigDecimal maxBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-bet")));
        BigDecimal minBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("min-bet")));
        return new SlotsGame.Config(slotsConfig, maxBet, minBet);
    }

    public WheelGame.Config getWheelConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("wheel"));
        BigDecimal maxBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-bet")));
        BigDecimal minBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("min-bet")));
        return new WheelGame.Config(gameSection.getStringList("items").stream().map(BigDecimal::new).toList(), maxBet, minBet);
    }

    public CrashGame.Config getCrashConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("crash"));
        BigDecimal maxMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-multiplier")));
        BigDecimal baseMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("base-multiplier")));
        BigDecimal crashChance = new BigDecimal(Objects.requireNonNull(gameSection.getString("crash-chance")));
        BigDecimal maxBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-bet")));
        BigDecimal minBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("min-bet")));
        return new CrashGame.Config(maxMultiplier, baseMultiplier, crashChance, maxBet, minBet);
    }

    public MinesGame.Config getMinesConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("mines"));
        BigDecimal baseMineChance = new BigDecimal(Objects.requireNonNull(gameSection.getString("base-mine-chance")));
        BigDecimal baseMinMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("base-min-multiplier")));
        BigDecimal baseMaxMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("base-max-multiplier")));
        ConfigurationSection difficultySection = Objects.requireNonNull(gameSection.getConfigurationSection("difficulty"));
        Map<Game.Difficulty, BigDecimal> difficultyMultipliers = Map.of(
                Game.Difficulty.EASY, new BigDecimal(Objects.requireNonNull(difficultySection.getString("easy"))),
                Game.Difficulty.NORMAL, new BigDecimal(Objects.requireNonNull(difficultySection.getString("normal"))),
                Game.Difficulty.HARD, new BigDecimal(Objects.requireNonNull(difficultySection.getString("hard")))
        );
        BigDecimal maxBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-bet")));
        BigDecimal minBet = new BigDecimal(Objects.requireNonNull(gameSection.getString("min-bet")));
        return new MinesGame.Config(baseMineChance, baseMinMultiplier, baseMaxMultiplier, difficultyMultipliers, maxBet, minBet);
    }

    public boolean isBigWinAnnounce() {
        return configuration.getBoolean("big-win-announce.enabled");
    }

    public double getBigWinMultiplier() {
        return configuration.getDouble("big-win-announce.aboveMultiplier");
    }

    public String getBigWinAnnounce(String username, String gameName, String formattedWinning, double multiplier) {
        return Objects.requireNonNull(configuration.getString("big-win-announce.command"), "")
                .replace("%username%", username)
                .replace("%game%", gameName)
                .replace("%winning%", formattedWinning)
                .replace("%multiplier%", String.valueOf(multiplier));
    }

    public boolean isDiscordWebhookEnabled() {
        return configuration.getBoolean("discord.enabled");
    }

    public String getDiscordWebhook() {
        return Objects.requireNonNull(configuration.getString("discord.webhook-url"));
    }

    public boolean isForceEconomyBridge() {
        return configuration.getBoolean("economy-bridge-force.enabled");
    }

    public String getForcedEconomyBridge() {
        return Objects.requireNonNull(configuration.getString("economy-bridge-force.bridge"));
    }

    public static MainConfig createDefault(Plugin plugin) {
        return new MainConfig(new File(plugin.getDataFolder(), "config.yml"), "/config/config.yml", plugin.getLogger());
    }

}
