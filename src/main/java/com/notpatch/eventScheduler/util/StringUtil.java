package com.notpatch.eventScheduler.util;

import com.notpatch.eventScheduler.task.ScheduleTask;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String FormatString(String s){

        s = colorized(s);
        s = s.replaceAll("%current_event%" , ScheduleTask.currentEvent);
        return s;
    }

    public static String colorized(String message) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
