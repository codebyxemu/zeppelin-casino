package xyz.zeppelin.casino.config;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.logging.Logger;

public class MessagesConfig extends BaseConfig {

    public MessagesConfig(File file, String defaultName, Logger logger) {
        super(file, defaultName, logger);
    }

    public String getInvalidNumber() {
        return getMessage("invalid-number");
    }

    public String getEnterBetAmount() {
        return getMessage("enter-bet-amount");
    }

    public String getMessage(String key) {
        return configuration.getString(key);
    }

    public static MessagesConfig createDefault(Plugin plugin) {
        return new MessagesConfig(new File(plugin.getDataFolder(), "messages.yml"), "/config/messages.yml", plugin.getLogger());
    }

    public String getRefundMessage() {
        return getMessage("refund");
    }

    public String getWinMessage(BigDecimal amount, BigDecimal multiplier) {
        String formattedAmount = DecimalFormat.getCurrencyInstance(Locale.US).format(amount);
        return getMessage("win").formatted(multiplier, formattedAmount);
    }

    public String getLoseMessage() {
        return getMessage("lose");
    }

    public String getBetCanNotBeLowerThanMinimum() {
        return getMessage("bet-cant-be-lower-than-minimum");
    }

    public String getBetCanNotBeHigherThanMaximum() {
        return getMessage("bet-cant-be-higher-than-maximum");
    }

    public String getInsufficientBalanceToBet() {
        return getMessage("insufficient-balance-to-bet");
    }
}
