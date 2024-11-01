package com.notpatch.eventScheduler.util;

import com.notpatch.eventScheduler.EventScheduler;
import org.bukkit.configuration.ConfigurationSection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventUtil {

    private final EventScheduler main;
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    public EventUtil(EventScheduler main) {
        this.main = main;
        String configDateFormat = main.getConfig().getString("DateFormat", "HH:mm:ss");
        this.timeFormat = new SimpleDateFormat(configDateFormat);
    }

    public Map<String, Long> findNearestEvent() {
        ConfigurationSection tasksSection = main.getConfig().getConfigurationSection("Tasks");
        if (tasksSection == null) {
            return null;
        }

        String nearestEvent = null;
        long nearestTimeDiff = Long.MAX_VALUE;

        Date now = new Date();

        Set<String> taskKeys = tasksSection.getKeys(false);
        for (String s : taskKeys) {
            String day = tasksSection.getString(s + ".Day");
            String eventTime = tasksSection.getString(s + ".Time");
            String eventName = tasksSection.getString(s + ".Event");

            if (day == null || eventTime == null || eventName == null) {
                continue;
            }

            boolean isToday = day.equalsIgnoreCase("Every") || dayFormat.format(now).equalsIgnoreCase(day);
            if (!isToday) {
                continue;
            }

            try {
                Date eventDate = timeFormat.parse(eventTime);
                if (eventDate == null) {
                    continue;
                }

                // Set the event time to today
                eventDate.setYear(now.getYear());
                eventDate.setMonth(now.getMonth());
                eventDate.setDate(now.getDate());

                long timeDiff = eventDate.getTime() - now.getTime();

                if (timeDiff > 0 && timeDiff < nearestTimeDiff) {
                    nearestTimeDiff = timeDiff;
                    nearestEvent = eventName;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (nearestEvent != null) {
            Map<String, Long> result = new HashMap<>();
            result.put(nearestEvent, nearestTimeDiff);
            return result;
        }

        return null;
    }


}
