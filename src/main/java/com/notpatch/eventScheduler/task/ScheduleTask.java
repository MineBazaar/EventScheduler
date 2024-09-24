package com.notpatch.eventScheduler.task;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ScheduleTask extends BukkitRunnable {

    EventScheduler main;

    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    public static String currentEvent = "";
    private String currentTask;

    public ScheduleTask(EventScheduler main){
        this.main = main;
        String configDateFormat = Objects.requireNonNullElse(main.getConfig().getString("DateFormat"), "HH:mm:ss");
        timeFormat = new SimpleDateFormat(configDateFormat);
    }


    @Override
    public void run() {
        FileConfiguration config = main.getConfig();
        ConfigurationSection tasksSection = config.getConfigurationSection("Tasks");

        if (tasksSection == null) {
            return;
        }

        String currentTime = timeFormat.format(new Date());

        for (String s : tasksSection.getKeys(false)) {
            String day = config.getString("Tasks." + s + ".Day");
            String event = StringUtil.colorized(config.getString("Tasks." + s + ".Event"));
            int duration = config.getInt("Tasks." + s + ".Duration", -1);
            int minPlayer = config.getInt("Tasks." + s + ".MinPlayer");
            String taskTime = config.getString("Tasks." + s + ".Time");

            if (day == null || event == null || duration == -1) {
                continue;
            }

            if (taskTime != null && taskTime.equals(currentTime)) {
                if (day.equalsIgnoreCase("Every") || dayFormat.format(new Date()).equals(day)) {
                    if(Bukkit.getOnlinePlayers().size() < minPlayer){
                        Bukkit.broadcastMessage(StringUtil.colorized(config.getString("MinPlayerMessage").replaceAll("%eventName%", event).replaceAll("%playerAmount%", String.valueOf(minPlayer))));
                        return;
                    }
                    currentTask = s;
                    currentEvent = event;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentEvent = "";
                        }
                    }.runTaskLater(main, duration * 20L);

                    for (String command : config.getStringList("Tasks." + s + ".Commands")) {
                        if (command == null || command.isEmpty()) {
                            break;
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), StringUtil.FormatString(command));
                        break;
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new ScheduleTask(main).startRepeatingTask();
                        }
                    }.runTaskLater(main, 20L);

                    cancel();
                    return;
                }
            }
        }
    }

    public void startRepeatingTask() {
        this.runTaskTimer(main, 0L, 1L);
    }




}
