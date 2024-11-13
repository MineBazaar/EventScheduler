package com.notpatch.eventScheduler;

public class Task {

    private final String day;
    private final String sound;
    private final String event;
    private final int duration;
    private final int minPlayer;
    private final String time;

    public Task(String day, String sound, String event, int duration, int minPlayer, String time) {
        this.day = day;
        this.sound = sound;
        this.event = event;
        this.duration = duration;
        this.minPlayer = minPlayer;
        this.time = time;
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

    public String getTime() {
        return time;
    }

}
