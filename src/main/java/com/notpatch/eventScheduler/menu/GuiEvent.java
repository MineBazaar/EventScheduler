package com.notpatch.eventScheduler.menu;

import com.notpatch.eventScheduler.EventScheduler;
import com.notpatch.eventScheduler.util.StringUtil;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.Collections;
import java.util.List;

public class GuiEvent extends FastInv {

    private final Configuration config = EventScheduler.getInstance().getConfigurationManager().getMenuConfiguration().getConfiguration();

    public GuiEvent() {
        super(EventScheduler.getInstance().getConfigurationManager().getMenuConfiguration().getConfiguration().getInt("menu.size"), StringUtil.colorized(EventScheduler.getInstance().getConfigurationManager().getMenuConfiguration().getConfiguration().getString("menu.title")));

        List<Integer> fillerSlots = config.getIntegerList("menu-filler-item.slots");
        Collections.sort(fillerSlots);
        for (int i = 0; i < EventScheduler.getInstance().getConfigurationManager().getMenuConfiguration().getConfiguration().getInt("menu.size"); i++) {
            if (fillerSlots.contains(i)) {
                setItem(i, new ItemBuilder(Material.valueOf(config.getString("menu-filler-item.material"))).name(" ").build());
            }
        }

        for (String key : config.getConfigurationSection("menu.items").getKeys(false)) {
            Material material = Material.valueOf(config.getString("menu.items." + key + ".material"));
            String name = StringUtil.colorized(config.getString("menu.items." + key + ".name"));
            List<String> lore = config.getStringList("menu.items." + key + ".lore");
            int slot = config.getInt("menu.items." + key + ".slot");
            setItem(slot, new ItemBuilder(material).name(name).lore(StringUtil.getColoredList(lore)).build());

        }
    }

}
