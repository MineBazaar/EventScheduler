package com.notpatch.eventScheduler.model;

import java.util.List;

public class SubTask {
    
    private final String time;
    private final List<String> commands;

    public SubTask(String time, List<String> commands) {
        this.time = time;
        this.commands = commands;
    }

    public String getTime() {
        return time;
    }

    public List<String> getCommands() {
        return commands;
    }
}