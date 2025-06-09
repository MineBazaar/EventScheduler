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

    private long parseTimeToMillis(String time, String day) {
        try {
            String[] timeParts = time.split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);

            switch (day.toUpperCase()) {
                case "MON":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    break;
                case "TUE":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    break;
                case "WED":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    break;
                case "THU":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    break;
                case "FRI":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    break;
                case "SAT":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    break;
                case "SUN":
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    break;
                case "EVERY":
                    break;
                default:
                    break;
            }

            return cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getNextTaskCountdown() {
        Task nextTask = getNextTask();
        if (nextTask != null) {
            String taskTime = nextTask.getTime();
            LocalTime nextEventTime = LocalTime.parse(taskTime);

            LocalTime currentTime = LocalTime.now();

            if (currentTime.isAfter(nextEventTime)) {
                return "";
            }

            long hours = Duration.between(currentTime, nextEventTime).toHours();
            long minutes = Duration.between(currentTime, nextEventTime).toMinutes() % 60;
            long seconds = Duration.between(currentTime, nextEventTime).getSeconds() % 60;
            long millis = Duration.between(currentTime, nextEventTime).toMillis() % 1000;

            return String.format("%02d:%02d:%02d", hours, minutes, seconds, millis);
        }
        return "";
    }

    public String getTaskCountdown(Task task) {
        if (task == null) return "";
        String taskTime = task.getTime();
        LocalTime taskEventTime = LocalTime.parse(taskTime);

        LocalTime currentTime = LocalTime.now();

        if (currentTime.isAfter(taskEventTime)) {
            return "";
        }

        long hours = Duration.between(currentTime, taskEventTime).toHours();
        long minutes = Duration.between(currentTime, taskEventTime).toMinutes() % 60;
        long seconds = Duration.between(currentTime, taskEventTime).getSeconds() % 60;
        long millis = Duration.between(currentTime, taskEventTime).toMillis() % 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds, millis);
    }

    public Task getNextTask() {
        List<Task> taskList = getTaskList();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }

        Task nearestTask = null;
        long nearestTimeDifference = Long.MAX_VALUE;

        long currentTimeMillis = System.currentTimeMillis();

        for (Task task : taskList) {
            String taskTime = task.getTime();
            String day = task.getDay();
            long taskTimeMillis = parseTimeToMillis(taskTime, day);

            long timeDifference = Math.abs(taskTimeMillis - currentTimeMillis);

            if (timeDifference < nearestTimeDifference) {
                nearestTimeDifference = timeDifference;
                nearestTask = task;
            }
        }

        return nearestTask;
    }

    public void upcomingTasks() {
        for (Task task : main.getTaskManager().getTaskList()) {
            if (!task.getDay().equalsIgnoreCase("Every") && !dayFormat.format(new Date()).equals(task.getDay())) {
                return;
            }
            for (SubTask subTask : task.getSubTasks()) {
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
            }
        }
    }

    //

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
