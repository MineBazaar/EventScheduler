package com.notpatch.eventScheduler.hook;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.model.Task;
import com.notpatch.eventScheduler.task.ScheduleTask;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        if (params.equalsIgnoreCase("event_current")) {
            return Objects.requireNonNullElse(ScheduleTask.currentEvent, "");
        }

        Task nextTask = EventScheduler.getInstance().getTaskManager().getNextTask();
        if (params.equalsIgnoreCase("event_next")) {

            return nextTask.getEvent();
        }

        if (params.equalsIgnoreCase("event_countdown")) {

            return EventScheduler.getInstance().getTaskManager().getNextTaskCountdown();

        }

        if (params.equalsIgnoreCase("event_time")) {
            return EventScheduler.getInstance().getTaskManager().getNextTaskTime();

        }
        return "";
    }
}
