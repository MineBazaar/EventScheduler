package com.notpatch.eventScheduler.util;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.hook.DiscordWebhook;
import org.bukkit.configuration.Configuration;

import java.awt.*;
import java.io.IOException;

public class DiscordUtil {

    public static void sendDiscordWebhook(){


        EventScheduler main = EventScheduler.getInstance();
        Configuration config = main.getConfig();

        if(!config.getBoolean("send-webhook")){
            return;
        }

        DiscordWebhook discordWebhook = new DiscordWebhook(config.getString("webhook-url", ""));

        discordWebhook.setContent(config.getString("webhook-content", ""));
        discordWebhook.setUsername(config.getString("webhook-username", "Event Scheduler"));
        discordWebhook.setAvatarUrl(config.getString("webhook-avatarURL", ""));

        if (config.getConfigurationSection("webhook-embeds") != null &&
                !config.getConfigurationSection("webhook-embeds").getKeys(false).isEmpty()) {

            if(config.getConfigurationSection("webhook-embeds").getKeys(false).size() > 10){
                return;
            }

            for (String section : config.getConfigurationSection("webhook-embeds").getKeys(false)) {
                DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

                String colorHex = config.getString("webhook-embeds." + section + ".color", "#000000");
                try {
                    embedObject.setColor(Color.decode(colorHex));
                } catch (NumberFormatException e) {
                    main.getLogger().warning("Error while parsing color: " + colorHex);
                    embedObject.setColor(Color.BLACK);
                }

                String fieldName = config.getString("webhook-embeds." + section + ".field.name");
                String fieldValue = config.getString("webhook-embeds." + section + ".field.value");
                boolean fieldInline = config.getBoolean("webhook-embeds." + section + ".field.inline", false);

                if (fieldName != null && !fieldName.isEmpty() && fieldValue != null && !fieldValue.isEmpty()) {
                    embedObject.addField(fieldName, fieldValue, fieldInline);
                } else {
                    main.getLogger().warning("Field name or value is empty.");
                }

                embedObject.setDescription(config.getString("webhook-embeds." + section + ".description", ""));
                embedObject.setTitle(config.getString("webhook-embeds." + section + ".title", ""));
                embedObject.setImage(config.getString("webhook-embeds." + section + ".imageURL", ""));
                embedObject.setUrl(config.getString("webhook-embeds." + section + ".URL", ""));

                discordWebhook.addEmbed(embedObject);
            }
        } else {
            main.getLogger().warning("Webhook embeds is empty.");
        }

        try {
            discordWebhook.execute();
        } catch (IOException e) {
            main.getLogger().severe("Discord webhook error: " + e.getMessage());
        }

    }

}
