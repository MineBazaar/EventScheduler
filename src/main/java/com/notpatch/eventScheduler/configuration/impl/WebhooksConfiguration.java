package com.notpatch.eventScheduler.configuration.impl;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.configuration.NConfiguration;

public class WebhooksConfiguration extends NConfiguration {

    public WebhooksConfiguration() {
        super(EventScheduler.getInstance(), "webhooks.yml");
    }
}
