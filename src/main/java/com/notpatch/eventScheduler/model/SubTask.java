package com.notpatch.eventScheduler.model;

import com.notpatch.eventScheduler.hook.DiscordWebhook;

import java.util.List;

public class SubTask {

    private final String time;
    private final List<String> commands;
    private final List<DiscordWebhook> webhooks;

    public SubTask(String time, List<String> commands, List<DiscordWebhook> webhooks) {
        this.time = time;
        this.commands = commands;
        this.webhooks = webhooks;
    }

    public String getTime() {
        return time;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<DiscordWebhook> getWebhooks() {
        return webhooks;
    }
}