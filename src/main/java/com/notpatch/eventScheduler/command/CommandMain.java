package com.notpatch.eventScheduler.command;

import com.notpatch.eventScheduler.EventScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandMain implements TabExecutor {

    EventScheduler main;

    public CommandMain(EventScheduler main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                if (p.hasPermission("eventscheduler.admin")) {
                    main.reloadConfig();
                    main.saveDefaultConfig();
                    main.saveConfig();
                    main.getTaskManager().loadTasks();
                    p.sendMessage("§eEvent Scheduler§7: §aConfig reloaded!");
                }
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("eventscheduler.admin")) {
                    main.reloadConfig();
                    main.saveDefaultConfig();
                    main.saveConfig();
                    main.getTaskManager().loadTasks();
                    p.sendMessage("§eEvent Scheduler§7: §aConfig reloaded!");
                }
            }
            if (args.length > 1 && args[0].equalsIgnoreCase("start")) {
                List<String> tasks = EventScheduler.getInstance().getTaskManager().getTasks().keySet().stream().toList();
                if (args.length == 2) {
                    if (tasks.contains(args[1])) {
                        EventScheduler.getInstance().getTaskManager().startTask(args[1]);
                        p.sendMessage("§eEvent Scheduler§7: §aTask started!");
                    } else {
                        p.sendMessage("§eEvent Scheduler§7: §cTask not found!");
                    }
                } else {
                    p.sendMessage("§eEvent Scheduler§7: §cInvalid arguments!");
                }
            }

        } else {
            main.reloadConfig();
            main.saveDefaultConfig();
            main.saveConfig();
            main.getTaskManager().loadTasks();
            sender.sendMessage("§eEvent Scheduler§7: §aConfig reloaded!");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("eventscheduler.admin"))
                return List.of("reload", "start");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            List<String> tasks = EventScheduler.getInstance().getTaskManager().getTasks().keySet().stream().toList();
            return tasks;
        }
        return List.of();
    }
}
