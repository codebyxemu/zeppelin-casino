package xyz.zeppelin.casino.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class Message {

    @Getter
    @Setter
    private String message;

    public Message colorize() {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
        return this;
    }

    public Message placeholder(String placeholder, String s) {
        this.message = placeholder.replaceAll(placeholder, s);
        return this;
    }



}
