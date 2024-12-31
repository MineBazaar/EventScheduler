package com.notpatch.eventScheduler.menu;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.model.Task;
import com.notpatch.eventScheduler.util.StringUtil;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiEvent extends FastInv {

    private final Configuration config = EventScheduler.getInstance().getConfig();

    private final List<ItemStack> requiredItems = new ArrayList<>();

    public GuiEvent(String title) {
        super(54, StringUtil.colorized(title));

        List<Integer> fillerSlots = config.getIntegerList("menu-filler-item.slots");
        Collections.sort(fillerSlots);
        for (int i = 0; i < 54; i++) {
            if (fillerSlots.contains(i)) {
                setItem(i, new ItemBuilder(Material.valueOf(config.getString("menu-filler-item.material"))).name(" ").build());
            }
        }
        int itemIndex = 0;
        for (String tasks : EventScheduler.getInstance().getTaskManager().getTasks().keySet()) {
            Task task = EventScheduler.getInstance().getTaskManager().getTask(tasks);
            ItemStack taskItem = new ItemBuilder(Material.valueOf(config.getString("menu-item")))
                    .name(StringUtil.colorized(config.getString("menu-item-name").replace("%taskId%", tasks)))
                    .lore(StringUtil.getColoredList(config.getStringList("menu-item-lore"), "%eventName%", task.getEvent(), "%eventTime%", task.getTime()))
                    .build();
            requiredItems.add(taskItem);
        }
        for (int i = 0; i < 54; i++) {
            if (!fillerSlots.contains(i) && itemIndex < requiredItems.size()) {
                setItem(i, requiredItems.get(itemIndex));
                itemIndex++;
            }
        }


    }

}
