package com.walrusone.skywarsreloaded.matchevents;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;

public class DisableRegenEvent extends MatchEvent {

	private BukkitTask br;
	
	public DisableRegenEvent(GameMap map, boolean b) {
		this.gMap = map;
		this.enabled = b;
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	    	eventName = "DisableRegenEvent";
	    	slot = 18;
	    	material = new ItemStack(Material.GOLDEN_APPLE, 1);
	        FileConfiguration fc = YamlConfiguration.loadConfiguration(mapFile);
	        this.min = fc.getInt("events." + eventName + ".minStart");
	        this.max = fc.getInt("events." + eventName + ".maxStart");
	        this.length = fc.getInt("events." + eventName + ".length");
	        this.chance = fc.getInt("events." + eventName + ".chance");
	        this.title = fc.getString("events." + eventName + ".title");
	        this.subtitle = fc.getString("events." + eventName + ".subtitle");
	        this.startMessage = fc.getString("events." + eventName + ".startMessage");
	        this.endMessage = fc.getString("events." + eventName + ".endMessage");
	        this.announceEvent = fc.getBoolean("events." + eventName + ".announceTimer");
	        this.repeatable = fc.getBoolean("events." + eventName + ".repeatable");
	    }
	}
	
	@Override
	public void doEvent() {
		if (gMap.getMatchState() == MatchState.PLAYING) {
			this.fired = true;
			sendTitle();
			gMap.setAllowRegen(false);
			if (length != -1) {
				br = new BukkitRunnable() {
					@Override
					public void run() {
						endEvent(false);
					}
				}.runTaskLater(SkyWarsReloaded.get(), length * 20L);
			}
		}
	}
	
	@Override
	public void endEvent(boolean force) {
		if (fired) {
			if (force && length != -1) {
				br.cancel();
			}
			gMap.setAllowRegen(true);
			if (gMap.getMatchState() == MatchState.PLAYING) {
				MatchManager.get().message(gMap, ChatColor.translateAlternateColorCodes('&', endMessage));
			}
			if (repeatable || force) {
				setStartTime();
				this.startTime = this.startTime + gMap.getTimer();
				this.fired = false;
			}
		}
	}
	
	@Override
	public void saveEventData() {
		File dataDirectory = SkyWarsReloaded.get().getDataFolder();
        File mapDataDirectory = new File(dataDirectory, "mapsData");

        if (!mapDataDirectory.exists() && !mapDataDirectory.mkdirs()) {
        	return;
        }
        
        File mapFile = new File(mapDataDirectory, gMap.getName() + ".yml");
	    if (mapFile.exists()) {
	        FileConfiguration fc = YamlConfiguration.loadConfiguration(mapFile);
	        fc.set("events." + eventName + ".enabled", this.enabled);
	        fc.set("events." + eventName + ".minStart", this.min);
	        fc.set("events." + eventName + ".maxStart", this.max);
	        fc.set("events." + eventName + ".length", this.length);
	        fc.set("events." + eventName + ".chance", this.chance);
	        fc.set("events." + eventName + ".title", this.title);
	        fc.set("events." + eventName + ".subtitle", this.subtitle);
	        fc.set("events." + eventName + ".startMessage",  this.startMessage);
	        fc.set("events." + eventName + ".endMessage", this.endMessage);
	        fc.set("events." + eventName + ".announceTimer", this.announceEvent);
	        fc.set("events." + eventName + ".repeatable", this.repeatable);
	        try {
				fc.save(mapFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

}
