package com.notpatch.eventScheduler.hook;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.EventUtil;
import com.notpatch.eventScheduler.task.ScheduleTask;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HookPlaceholderAPI extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "eventscheduler";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NotPatch";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";

    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if(params.equalsIgnoreCase("currentEvent")){
            return ScheduleTask.currentEvent;
        }
        EventUtil eventUtil = new EventUtil(EventScheduler.getInstance());
        Map<String, Long> nearestEvent = eventUtil.findNearestEvent();
        if(params.equalsIgnoreCase("nextEvent")){

            String event = nearestEvent.keySet().iterator().next();
            return event;
        }
        if(params.equalsIgnoreCase("nextEventTime")){
            String event = nearestEvent.keySet().iterator().next();
            long timeRemaining = nearestEvent.get(event);
            // 564534378
            long hour = timeRemaining / 1000 / 60 / 60;
            timeRemaining = timeRemaining - hour * 1000 * 60 * 60;
            long minute = timeRemaining / 1000 / 60;
            // 1 saat, 32dk
            //long hours = timeRemaining / 1000 / 60 / 60;
            return String.format("%d h, %d, m",
                    hour,
                    minute);

        }
        return "";
    }
}
