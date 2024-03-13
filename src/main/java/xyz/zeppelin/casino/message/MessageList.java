package xyz.zeppelin.casino.message;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MessageList {

    @Getter
    private List<String> messages;

    public MessageList(List<String> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public MessageList(String[] messages) {
        this.messages = new ArrayList<>();
        for (String message : messages) {
            this.messages.add(message);
        }
    }

    public MessageList colorize() {
        List<String> coloredMessages = new ArrayList<>();
        for (String message : messages) {
            coloredMessages.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        this.messages = coloredMessages;
        return this;
    }

    public MessageList placeholder(String placeholder, String s) {
        List<String> replacedMessages = new ArrayList<>();
        for (String message : messages) {
            replacedMessages.add(message.replaceAll(placeholder, s));
        }
        this.messages = replacedMessages;
        return this;
    }
}
