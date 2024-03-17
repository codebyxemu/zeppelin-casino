package xyz.zeppelin.casino.game;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.zeppelin.casino.ZeppelinCasinoPlugin;
import xyz.zeppelin.casino.component.ComponentManager;
import xyz.zeppelin.casino.config.MainConfig;
import xyz.zeppelin.casino.discord.DiscordWebhook;
import xyz.zeppelin.casino.message.Message;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Represents a single player game session.
 */
@Getter
public abstract class SinglePlayerGameSession<T extends Game> extends BaseGameSession<T> {

    protected final Plugin plugin;
    protected final Player player;
    protected final PlayerBetManager betManager;

    protected MainConfig mainConfig;

    protected boolean bigWinAnnounce;
    protected double bigWinMultiplier;

    protected boolean discordWebhookEnabled;
    protected String discordWebhookUrl;

    public SinglePlayerGameSession(PlayerBetManager playerBetManager) {
        this.plugin = playerBetManager.getPlugin();
        this.player = playerBetManager.getPlayer();
        this.betManager = playerBetManager;

        this.mainConfig = ComponentManager.getComponentManager(plugin).getComponent(MainConfig.class);

        this.bigWinAnnounce = mainConfig.isBigWinAnnounce();
        this.bigWinMultiplier = mainConfig.getBigWinMultiplier();

        this.discordWebhookEnabled = mainConfig.isDiscordWebhookEnabled();
        this.discordWebhookUrl = mainConfig.getDiscordWebhook();
    }

    protected final void openSummaryUI(String gameName, boolean isWin, Consumer<PlayerBetManager> repeat) {
        GameSummaryUserInterface.open(betManager, repeat, gameName, isWin);

        // TODO: Discord webhook

        if (bigWinAnnounce) {

            if (isWin && betManager.getMultiplier().compareTo(BigDecimal.valueOf(bigWinMultiplier)) >= 0) {
                String formattedWinning = DecimalFormat.getCurrencyInstance(Locale.US).format(betManager.calculateWinning());


                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                String command = mainConfig.getBigWinAnnounce(
                        player.getName(),
                        gameName,
                        formattedWinning,
                        Math.round(betManager.getMultiplier().doubleValue())
                );

                // TODO: Remove
                Bukkit.getLogger().info(command);



                Bukkit.dispatchCommand(console, command);

            }
        }

        if (discordWebhookEnabled) {
            if (!discordWebhookUrl.isEmpty()) {
                return;
            }

            String formattedWinning = DecimalFormat.getCurrencyInstance(Locale.US).format(betManager.calculateWinning());

            DiscordWebhook webhook = new DiscordWebhook(discordWebhookUrl);
            DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

            embedObject.setTitle("Game has been completed");
            embedObject.setDescription("A player has completed!");
            embedObject.addField("Player", player.getName(), true);
            embedObject.addField("Game", gameName, true);
            embedObject.addField("Return on Bet", formattedWinning, true);
            embedObject.addField("Multiplier", "x" + Math.round(betManager.getMultiplier().doubleValue()), true);
            embedObject.setColor(Color.orange);

            webhook.setUsername("Zeppelin Casino");
            webhook.setContent("A bet was placed and completed.");
            webhook.setEmbeds(List.of(embedObject));

            try {
                webhook.execute();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Zeppelin Casino could not send a Discord webhook. Please check your configuration.");
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public final List<Player> getPlayers() {
        return ImmutableList.of(player);
    }
}
