package com.notpatch.eventScheduler.manager;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.hook.DiscordWebhook;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.*;
import java.util.HashMap;

public class WebhookManager {

    private EventScheduler eventScheduler;

    public WebhookManager(EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
    }

    private HashMap<String, DiscordWebhook> webhooks = new HashMap<>();

    public HashMap<String, DiscordWebhook> getWebhooks() {
        return webhooks;
    }

    public void loadWebhooks() {
        webhooks.clear();
        Configuration config = eventScheduler.getConfigurationManager().getWebhooksConfiguration().getConfiguration();
        ConfigurationSection section = config.getConfigurationSection("webhooks");

        if (section != null) {
            for (String id : section.getKeys(false)) {
                String path = "webhooks." + id;

                DiscordWebhook webhook = new DiscordWebhook(config.getString(path + ".url"));
                webhook.setContent(config.getString(path + ".content"));

                if (config.getString(path + ".username") == null) {
                    webhook.setUsername("EventScheduler");
                } else {
                    webhook.setUsername(config.getString(path + ".username"));
                }

                if (config.getString(path + ".avatarURL") != null) {
                    webhook.setAvatarUrl(config.getString(path + ".avatarURL"));
                }


                ConfigurationSection embeds = config.getConfigurationSection(path + ".embeds");
                if (embeds != null) {
                    for (String embedId : embeds.getKeys(false)) {
                        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                        String base = path + ".embeds." + embedId;

                        String color = config.getString(base + ".color", "#000000");
                        try {
                            embed.setColor(Color.decode(color));
                        } catch (Exception e) {
                            embed.setColor(Color.BLACK);
                        }

                        embed.setTitle(config.getString(base + ".title", ""));
                        embed.setDescription(config.getString(base + ".description", ""));
                        embed.setUrl(config.getString(base + ".URL", ""));
                        embed.setImage(config.getString(base + ".imageURL", ""));

                        String fieldName = config.getString(base + ".field.name");
                        String fieldValue = config.getString(base + ".field.value");
                        boolean fieldInline = config.getBoolean(base + ".field.inline", false);

                        if (fieldName != null && fieldValue != null) {
                            embed.addField(fieldName, fieldValue, fieldInline);
                        }

                        webhook.addEmbed(embed);
                    }
                }
                webhooks.put(id, webhook);
            }
        }
    }

}
