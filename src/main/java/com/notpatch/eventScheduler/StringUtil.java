package com.notpatch.eventScheduler;

import com.notpatch.eventScheduler.task.ScheduleTask;
import org.bukkit.ChatColor;

public class StringUtil {

    public static String FormatString(String s){

        s = ChatColor.translateAlternateColorCodes('&', s);
        s = s.replaceAll("%current_event%" , ScheduleTask.currentEvent);
        return s;
    }

}
