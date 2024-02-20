package xyz.zeppelin.casino.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.game.Game;
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
        return new SlotsGame.Config(slotsConfig);
    }

    public WheelGame.Config getWheelConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("wheel"));
        return new WheelGame.Config(gameSection.getStringList("items").stream().map(BigDecimal::new).toList());
    }

    public CrashGame.Config getCrashConfig() {
        ConfigurationSection gameSection = Objects.requireNonNull(configuration.getConfigurationSection("crash"));
        BigDecimal maxMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("max-multiplier")));
        BigDecimal baseMultiplier = new BigDecimal(Objects.requireNonNull(gameSection.getString("base-multiplier")));
        BigDecimal crashChance = new BigDecimal(Objects.requireNonNull(gameSection.getString("crash-chance")));
        return new CrashGame.Config(maxMultiplier, baseMultiplier, crashChance);
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
        return new MinesGame.Config(baseMineChance, baseMinMultiplier, baseMaxMultiplier, difficultyMultipliers);
    }

    public static MainConfig createDefault(Plugin plugin) {
        return new MainConfig(new File(plugin.getDataFolder(), "config.yml"), "/config/config.yml", plugin.getLogger());
    }
}
