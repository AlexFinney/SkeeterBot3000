package com.skeeter144.script;

import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;

import com.skeeter144.filter.SBUFilters;
import com.skeeter144.id.InventoryIds;
import com.skeeter144.id.NpcIds;
import com.skeeter144.id.SBUIds;
import com.skeeter144.sleep.Sleep;

public class SBFisher extends SkeeterScript {

	public SBFisher(Script script) {
		super("Skeeter's Fish n' Cook", script);
	}
	
	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}
	
	void fish(FishingType type) {
		int fishingSpotId = 0;
		if(type == FishingType.SMALL_NET) fishingSpotId = NpcIds.NET_FISHING_SPOT;
		else if(type == FishingType.FLY) fishingSpotId = NpcIds.ROD_FISHING_SPOT;
		
		NPC fishingSpot = script.getNpcs().closest(fishingSpotId);
		if(fishingSpot != null) {
			if(type == FishingType.SMALL_NET && fishingSpot.hasAction("Small Net"))
				fishingSpot.interact("Small Net");
			
			if(type == FishingType.BIG_NET && fishingSpot.hasAction("Net"))
				fishingSpot.interact("Net");
			
			if(type == FishingType.FLY && fishingSpot.hasAction("Lure"))
				fishingSpot.interact("Lure");
			
			if(type == FishingType.BAIT && fishingSpot.hasAction("Bait"))
				fishingSpot.interact("Bait");
			
			if(type == FishingType.HARPOON && fishingSpot.hasAction("Harpoon"))
				fishingSpot.interact("Harpoon");
			
			Sleep.sleepUntil(() -> script.myPlayer().getAnimation() == 623, 10000);
		}
	}
	
	void dropFish() {
		Inventory inv = script.getInventory();
		logger.debug("Dropping fish");
		inv.dropAll(InventoryIds.BURNT_SHRIMP, InventoryIds.COOKED_SHRIMP, InventoryIds.RAW_SHRIMP);
		logger.debug("all fish dropped");
	}
	
	

	@SuppressWarnings("unchecked")
	public Action nextAction() {
		Player p = script.myPlayer();
		Inventory i = script.getInventory();
		
		if(i.isFull()) {
			if(i.contains(SBUFilters.LOG_FILTER)) {
				return Action.LIGHT_FIRE;
			}else {
				return Action.CHOP_TREE;
			}
		}
		
		if(i.isFull() || i.getEmptySlots() == 1) {
			if(script.objects.closest("Fire") != null) return Action.COOK_FOOD;
		}
		
		if(p.isAnimating() || p.isMoving() || idleTime() < 2) return Action.WAIT;
		
		return Action.FISH_SPOT;
	}
	
	enum FishingType{
		SMALL_NET,
		BIG_NET,
		BAIT,
		FLY,
		HARPOON,
		CAGE
	}

	@Override
	public int executeAction(Action action) throws InterruptedException {
		Inventory inv = script.getInventory();
		
		currentAction = nextAction();

		switch (currentAction) {
			case FISH_SPOT:
				fish(FishingType.FLY);
				break;
			case CHOP_TREE:
				RS2Object tree = script.objects.closest(SBUIds.PLAIN_WOOD_TREES);
				if(tree != null) {
					if(inv.isFull())
						inv.interact(27, "Drop");
					
					tree.interact("Chop down");
				}
				break;
			case LIGHT_FIRE:
				if(inv.contains("Tinderbox")) {
					if(inv.isItemSelected())
						inv.deselectItem();
					
					inv.interact(inv.getSlot("Tinderbox"), "Use");
					sleep(random(100, 500));
					inv.interact("Use", "Logs");
				}
				break;
			case COOK_FOOD:
				RS2Widget cookWindow = script.getWidgets().get(270, 14);
				if(cookWindow != null && cookWindow.isVisible()) {
					cookWindow.interact("Cook All");
					Sleep.sleepUntil(() -> idleTime() > 3 || !inv.contains(SBUFilters.UNCOOKED_FISH), 30000);
				}
				
				RS2Object fire = script.objects.closest("Fire");
				if(fire != null && fire.getPosition().distance(script.myPosition()) < 5) {
					inv.interact(inv.getSlot(SBUFilters.UNCOOKED_FISH), "Use");
					sleep(random(100, 500));
					fire.interact();
				}
			default:
				break;
		}
		
		return 2000;
	}
}
