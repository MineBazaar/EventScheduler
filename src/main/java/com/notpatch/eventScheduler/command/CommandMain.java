package com.notpatch.eventScheduler.command;

import com.notpatch.eventScheduler.EventScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandMain implements CommandExecutor {

    EventScheduler main;

    public CommandMain(EventScheduler main){
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;

            if(p.hasPermission("eventscheduler.admin")){
                main.reloadConfig();
                main.saveDefaultConfig();
                main.saveConfig();
                main.getTaskManager().loadTasks();
                p.sendMessage("§eEvent Scheduler§7: §aConfig reloaded!");

            }
        }else{
            main.reloadConfig();
            main.saveDefaultConfig();
            main.saveConfig();
            main.getTaskManager().loadTasks();
            sender.sendMessage("§eEvent Scheduler§7: §aConfig reloaded!");
        }
        return false;
    }
}
