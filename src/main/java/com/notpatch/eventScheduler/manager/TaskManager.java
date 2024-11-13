package com.notpatch.eventScheduler.manager;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.Task;
import com.notpatch.eventScheduler.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class TaskManager {

    EventScheduler main;

    private final HashMap<String, Task> tasks = new HashMap<>();

    public TaskManager(EventScheduler main){
        this.main = main;
    }

    public void loadTasks(){
        tasks.clear();
        ConfigurationSection tasksSection = main.getConfig().getConfigurationSection("Tasks");

        if (tasksSection == null) return;

        for (String s : tasksSection.getKeys(false)) {
            String day = tasksSection.getString(s + ".day");
            String sound = tasksSection.getString(s + ".sound");
            String event = StringUtil.colorized(tasksSection.getString(s + ".event"));
            int duration = tasksSection.getInt(s + ".duration", -1);
            int minPlayer = tasksSection.getInt(s + ".minPlayer");
            String taskTime = tasksSection.getString(s + ".time");

            if (day != null && event != null && duration != -1) {
                tasks.put(s, new Task(day, sound, event, duration, minPlayer, taskTime));
            }
        }
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }
}
