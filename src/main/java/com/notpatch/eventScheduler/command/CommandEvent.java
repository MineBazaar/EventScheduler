package com.notpatch.eventScheduler.command;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.menu.GuiEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandEvent implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (p.hasPermission("eventscheduler.menu")) {
                new GuiEvent(EventScheduler.getInstance().getConfig().getString("menu-name")).open((Player) sender);
            }
        }
        return false;
    }
}
