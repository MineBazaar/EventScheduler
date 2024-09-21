package com.notpatch.eventScheduler;

import com.notpatch.eventScheduler.command.CommandMain;
import com.notpatch.eventScheduler.hook.HookPlaceholderAPI;
import com.notpatch.eventScheduler.task.ScheduleTask;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventScheduler extends JavaPlugin {

    @Override
    public void onEnable() {
        saveConfig();

        if(usePlaceholderAPI()){
            new HookPlaceholderAPI().register();
        }

        ScheduleTask task = new ScheduleTask(this);
        task.startRepeatingTask();

        getCommand("eventscheduler").setExecutor(new CommandMain(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private boolean usePlaceholderAPI(){
        if(getConfig().getBoolean("UsePlaceholderAPI")){
            Plugin plugin = getServer().getPluginManager().getPlugin("PlaceholderAPI");
            if(plugin != null || (plugin instanceof PlaceholderAPIPlugin)) {
                return true;
            }
        }
        return false;
    }

}
