package com.notpatch.eventScheduler.manager;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.hook.DiscordWebhook;
import com.notpatch.eventScheduler.model.SubTask;
import com.notpatch.eventScheduler.model.Task;
import com.notpatch.eventScheduler.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

public class TaskManager {

    private final EventScheduler main;

    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    public final List<String> executed = new ArrayList<>();
    private final HashMap<String, Task> tasks = new HashMap<>();
    private final List<Task> taskList = new ArrayList<>();

    public TaskManager(EventScheduler main) {
        this.main = main;
    }

    public void loadTasks() {
        tasks.clear();
        taskList.clear();
        executed.clear();

        ConfigurationSection tasksSection = main.getConfig().getConfigurationSection("Tasks");
        if (tasksSection == null) return;

        for (String s : tasksSection.getKeys(false)) {
            String day = tasksSection.getString(s + ".day");
            String sound = tasksSection.getString(s + ".sound");
            String event = StringUtil.colorized(tasksSection.getString(s + ".event"));
            int duration = tasksSection.getInt(s + ".duration", -1);
            int minPlayer = tasksSection.getInt(s + ".minPlayer");
            String taskTime = tasksSection.getString(s + ".time");
            List<String> commands = tasksSection.getStringList(s + ".commands");

            List<DiscordWebhook> webhooks = new ArrayList<>();
            List<String> webhookIds = tasksSection.getStringList(s + ".webhooks");

            for (String webhookId : webhookIds) {
                DiscordWebhook webhook = main.getWebhookManager().getWebhooks().get(webhookId);
                if (webhook != null) {
                    webhooks.add(webhook);
                }
            }
            List<SubTask> subTasks = new ArrayList<>();
            ConfigurationSection subTasksSection = tasksSection.getConfigurationSection(s + ".subtasks");
            if (subTasksSection != null) {
                for (String subKey : subTasksSection.getKeys(false)) {
                    String subTime = subTasksSection.getString(subKey + ".time");
                    List<String> subCommands = subTasksSection.getStringList(subKey + ".commands");
                    List<DiscordWebhook> subWebhooks = new ArrayList<>();
                    List<String> subWebhookIds = subTasksSection.getStringList(subKey + ".webhooks");
                    for (String subWebhookId : subWebhookIds) {
                        DiscordWebhook webhook = main.getWebhookManager().getWebhooks().get(subWebhookId);
                        if (webhook != null) {
                            subWebhooks.add(webhook);
                        }
                    }
                    subTasks.add(new SubTask(subTime, subCommands, subWebhooks));
                }
            }

            if (day != null && event != null && duration != -1) {
                Task task = new Task(day, sound, event, duration, minPlayer, taskTime, commands, subTasks, webhooks);
                tasks.put(s, task);
            }
        }

        tasks.forEach((key, value) -> taskList.add(value));
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public String getTaskId(Task task) {
        for (String key : tasks.keySet()) {
            if (tasks.get(key).equals(task)) {
                return key;
            }
        }
        return null;
    }

    public Task getTaskByName(String name) {
        for (String task : tasks.keySet()) {
            if (task.equalsIgnoreCase(name)) {
                return tasks.get(task);
            }
        }
        return null;
    }

    public Task getTask(String key) {
        return tasks.get(key);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public String getNextTaskTime() {
        Task task = getNextTask();
        if (task == null) return null;
        return task.getTime();
    }

    private DayOfWeek getDayOfWeek(String day) {
        switch (day.toUpperCase()) {
            case "MON": return DayOfWeek.MONDAY;
            case "TUE": return DayOfWeek.TUESDAY;
            case "WED": return DayOfWeek.WEDNESDAY;
            case "THU": return DayOfWeek.THURSDAY;
            case "FRI": return DayOfWeek.FRIDAY;
            case "SAT": return DayOfWeek.SATURDAY;
            case "SUN": return DayOfWeek.SUNDAY;
            default: return null;
        }
    }

    private LocalDateTime getNextOccurrence(String day, LocalTime time) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtTime = now.toLocalDate().atTime(time);

        if (day.equalsIgnoreCase("EVERY")) {
            if (now.isBefore(todayAtTime)) {
                return todayAtTime;
            } else {
                return todayAtTime.plusDays(1);
            }
        }

        DayOfWeek targetDay = getDayOfWeek(day);
        if (targetDay == null) return null;

        LocalDate today = LocalDate.now();
        LocalDate nextDate = today;

        while (nextDate.getDayOfWeek() != targetDay) {
            nextDate = nextDate.plusDays(1);
        }

        LocalDateTime nextDateTime = nextDate.atTime(time);

        if (nextDate.equals(today) && now.isAfter(nextDateTime)) {
            nextDateTime = nextDateTime.plusWeeks(1);
        }

        return nextDateTime;
    }

    public String getNextTaskCountdown() {
        Task nextTask = getNextTask();
        if (nextTask == null) return "";

        return getTaskCountdown(nextTask);
    }

    public String getTaskCountdown(Task task) {
        if (task == null || task.getTime() == null) return "";

        try {
            LocalTime taskTime = LocalTime.parse(task.getTime());
            LocalDateTime nextOccurrence = getNextOccurrence(task.getDay(), taskTime);

            if (nextOccurrence == null) return "";

            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, nextOccurrence);

            if (duration.isNegative()) return "";

            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public Task getNextTask() {
        List<Task> taskList = getTaskList();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }

        Task nearestTask = null;
        LocalDateTime nearestDateTime = null;
        LocalDateTime now = LocalDateTime.now();

        for (Task task : taskList) {
            String taskTime = task.getTime();
            if (taskTime == null) continue;

            try {
                LocalTime time = LocalTime.parse(taskTime);
                LocalDateTime nextOccurrence = getNextOccurrence(task.getDay(), time);

                if (nextOccurrence != null && nextOccurrence.isAfter(now)) {
                    if (nearestDateTime == null || nextOccurrence.isBefore(nearestDateTime)) {
                        nearestDateTime = nextOccurrence;
                        nearestTask = task;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return nearestTask;
    }

    public void upcomingTasks() {
        for (Task task : main.getTaskManager().getTaskList()) {
            if (!task.getDay().equalsIgnoreCase("Every")) {
                String currentDay = dayFormat.format(new Date()).toUpperCase();
                if (!currentDay.equals(task.getDay().substring(0, 3).toUpperCase())) {
                    continue;
                }
            }

            for (SubTask subTask : task.getSubTasks()) {
                try {
                    LocalTime subTaskTime = LocalTime.parse(subTask.getTime());
                    LocalTime currentTime = LocalTime.now().withNano(0);

                    if (executed.contains(subTask.getTime())) continue;

                    if (currentTime.equals(subTaskTime)) {
                        for (String command : subTask.getCommands()) {
                            if (command == null || command.isEmpty()) continue;
                            main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command);
                        }

                        for (DiscordWebhook discordWebhook : subTask.getWebhooks()) {
                            try {
                                discordWebhook.execute();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        executed.add(subTask.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startTask(String taskName) {
        Task task = tasks.get(taskName);
        if (task == null) return;

        if (task.getCommands() != null && !task.getCommands().isEmpty()) {
            for (String command : task.getCommands()) {
                main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command);
            }
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
    }

    public void resetExecuted() {
        executed.clear();
    }

    public List<String> getExecuted() {
        return executed;
    }
}