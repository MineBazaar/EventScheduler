package com.notpatch.eventScheduler.model;

import java.util.List;

public class Task {

    private final String day;
    private final String sound;
    private final String event;
    private final int duration;
    private final int minPlayer;
    private final String time;
    private List<SubTask> subTasks;
    private List<String> commands;

    public Task(String day, String sound, String event, int duration, int minPlayer, String time, List<String> commands, List<SubTask> subTasks) {
        this.day = day;
        this.sound = sound;
        this.event = event;
        this.duration = duration;
        this.minPlayer = minPlayer;
        this.time = time;
        this.commands = commands;
        this.subTasks = subTasks;
    }

    public String getDay() {
        return day;
    }

    public String getSound() {
        return sound;
    }

    public String getEvent() {
        return event;
    }

    public int getDuration() {
        return duration;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public String getTime() {
        return time;
    }

    public List<String> getCommands() {
        return commands;
    }
}
