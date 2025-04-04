package com.notpatch.eventScheduler.configuration.impl;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.configuration.NConfiguration;

public class MenuConfiguration extends NConfiguration {

    public MenuConfiguration() {
        super(EventScheduler.getInstance(), "menu.yml");
    }
}
