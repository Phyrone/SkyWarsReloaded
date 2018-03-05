package com.walrusone.skywarsreloaded.menus;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;

public class IconMenu {

	private ArrayList<Inventory> invs;
    private OptionClickEventHandler handler;
    private Runnable update;


    public IconMenu(ArrayList<Inventory> invs, OptionClickEventHandler optionClickEventHandler) {
        this.invs = invs;
        for (int i = 0; i < invs.size(); i++) {
        	addExitItem(invs.get(i));
        	if (i >= 0 && invs.size() > 0 && (i+1) < invs.size()) {
        		addNextItem(invs.get(i));
        	}
        	if (i > 0 && i < invs.size()) {
        		addPrevItem(invs.get(i));
        	}
        }
        this.handler = optionClickEventHandler;
    }

    private void addPrevItem(Inventory inventory) {
		inventory.setItem(inventory.getSize() - 9, SkyWarsReloaded.getIM().getItem("prevPageItem"));
	}

	private void addNextItem(Inventory inventory) {
		inventory.setItem(inventory.getSize() - 1, SkyWarsReloaded.getIM().getItem("nextPageItem"));	
	}

	private void addExitItem(Inventory inventory) {
		inventory.setItem(inventory.getSize() - 5, SkyWarsReloaded.getIM().getItem("exitMenuItem"));		
	}
	
    public void update() {
    	if (SkyWarsReloaded.get().isEnabled()) {
        	Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), update);
    	}
    }
    
    public void setUpdate(Runnable update2) {
    	update = update2;
    }

	public void onInventoryClick(InventoryClickEvent event) {       
        int index = invs.lastIndexOf(event.getInventory());
        if (index == -1) {
        	return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
 
        try {
            if (!(slot >= 0 && slot < invs.get(index).getSize())) {
                return;
            }
        } catch (NullPointerException e) {
        	return;
        }

        String name = "uselessName";
        if (event.getInventory().getItem(slot) != null && !event.getInventory().getItem(slot).getType().equals(Material.AIR)) {
            name = SkyWarsReloaded.getNMS().getItemName(event.getCurrentItem());
        }     

        if (!name.equalsIgnoreCase("uselessName")) {
            if (name.equalsIgnoreCase(SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("prevPageItem"))) && index + 1 >= 0) {
            	openInventory((Player) event.getWhoClicked(), index - 1);
            	return;
            }
            if (name.equalsIgnoreCase(SkyWarsReloaded.getNMS().getItemName(SkyWarsReloaded.getIM().getItem("nextPageItem"))) && index + 1 < invs.size()) {
            	openInventory((Player) event.getWhoClicked(), index + 1);
            	return;
            }
            OptionClickEvent clickEvent = new OptionClickEvent((Player) event.getWhoClicked(), name, event.getClick(), event.getCurrentItem().clone(), event.getRawSlot());
            handler.onOptionClick(clickEvent);
        }
    }
    
	public Inventory getInventory(int index) {
		return invs.get(index);
	}
	
	public ArrayList<Inventory> getInventories() {
		return invs;
	}
	
	public void openInventory(Player player, int index) {
		player.openInventory(invs.get(index));
	}

    public static class OptionClickEvent {

        private Player player;
        private String name;
        private ClickType clickType;
        private ItemStack item;
        private int slot;

        public OptionClickEvent(Player player, String name, ClickType clickType, ItemStack itemStack, int slot) {
            this.player = player;
            this.name = name;
            this.clickType = clickType;
            this.item = itemStack;
            this.slot = slot;
        }

        public Player getPlayer() {
            return this.player;
        }

        public String getName() {
            return this.name;
        }
        
        public ClickType getClick() {
            return this.clickType;
        }
        
        public ItemStack getItem() {
        	return item;
        }
        
        public int getSlot() {
        	return slot;
        }       
    }
    

	public interface OptionClickEventHandler {
		void onOptionClick(OptionClickEvent clickEvent);
	}

}