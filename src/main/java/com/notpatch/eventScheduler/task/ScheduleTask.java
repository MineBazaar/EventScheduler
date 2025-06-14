package com.notpatch.eventScheduler.task;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.hook.DiscordWebhook;
import com.notpatch.eventScheduler.model.Task;
import com.notpatch.eventScheduler.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class ScheduleTask extends BukkitRunnable {

    EventScheduler main;

    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    public static String currentEvent = "";

    public ScheduleTask(EventScheduler main) {
        this.main = main;
        String configDateFormat = Objects.requireNonNullElse(main.getConfig().getString("DateFormat"), "HH:mm:ss");
        timeFormat = new SimpleDateFormat(configDateFormat);
    }


    @Override
    public void run() {
        String currentTime = timeFormat.format(new Date());

        main.getTaskManager().upcomingTasks();

        for (Map.Entry<String, Task> entry : main.getTaskManager().getTasks().entrySet()) {
            Task task = entry.getValue();
            if (task.getTime().equals(currentTime)) {
                if (task.getDay().equalsIgnoreCase("Every") || dayFormat.format(new Date()).equals(task.getDay())) {
                    if (Bukkit.getOnlinePlayers().size() < task.getMinPlayer()) {
                        Bukkit.broadcastMessage(StringUtil.colorized(main.getConfig().getString("MinPlayerMessage")
                                .replaceAll("%eventName%", task.getEvent())
                                .replaceAll("%playerAmount%", String.valueOf(task.getMinPlayer()))));
                        break;
                    }

                    currentEvent = task.getEvent();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentEvent = "";
                        }
                    }.runTaskLater(main, task.getDuration() * 20L);

                    if (task.getSound() != null) {
                        Bukkit.getOnlinePlayers().forEach(player ->
                                player.playSound(player.getLocation(), Sound.valueOf(task.getSound()), 1F, 1F)
                        );
                    }
                    if (task.getWebhooks() != null && !task.getWebhooks().isEmpty()) {
                        for (DiscordWebhook discordWebhook : task.getWebhooks()) {
                            try {
                                discordWebhook.execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    if (task.getCommands() != null && !task.getCommands().isEmpty()) {
                        for (String command : task.getCommands()) {
                            if (command == null || command.isEmpty()) {
                                continue;
                            }

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), StringUtil.FormatString(command));

                            break;
                        }
                    }

                    main.getTaskManager().resetExecuted();

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
