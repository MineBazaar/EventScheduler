package com.notpatch.eventScheduler.manager;

import com.notpatch.eventScheduler.configuration.NConfiguration;
import com.notpatch.eventScheduler.configuration.impl.MenuConfiguration;
import com.notpatch.eventScheduler.configuration.impl.WebhooksConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {

    private final List<NConfiguration> configurations = new ArrayList<>();

    private final MenuConfiguration menuConfiguration;
    private final WebhooksConfiguration webhooksConfiguration;


    public ConfigurationManager() {
        configurations.add(menuConfiguration = new MenuConfiguration());
        configurations.add(webhooksConfiguration = new WebhooksConfiguration());
    }

    public void loadConfigurations() {
        for (NConfiguration configuration : configurations) {
            configuration.loadConfiguration();
        }
    }

    public void saveConfigurations() {
        for (NConfiguration configuration : configurations) {
            configuration.saveConfiguration();
        }
    }

    public MenuConfiguration getMenuConfiguration() {
        return menuConfiguration;
    }

    public WebhooksConfiguration getWebhooksConfiguration() {
        return webhooksConfiguration;
    }
}