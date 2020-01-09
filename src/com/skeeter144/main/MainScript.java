package com.skeeter144.main;

import java.awt.Graphics2D;

import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import com.skeeter144.filter.SBUFilters;
import com.skeeter144.id.InventoryIds;
import com.skeeter144.id.NpcIds;
import com.skeeter144.id.SBUIds;
import com.skeeter144.misc.Formatting;
import com.skeeter144.script.SkeeterScript.Action;
import com.skeeter144.script.SkeeterScript.State;
import com.skeeter144.sleep.Sleep;

@ScriptManifest(name = "GenericFisher", author = "TheGreatBabushka", version = 1.0, info = "", logo = "")
public class MainScript extends Script{
	
	long lastAnimationTime = 0;
	Action lastAction;
	Action currentAction;
	
	@Override
	public void onStart() throws InterruptedException {
		super.onStart();
		this.getBot().addMessageListener(this);
		
		final MainScript script = this;
		Thread t = new Thread(new Runnable() {
			public void run() {
				while(true) {
					Player p = script.myPlayer();
					if(p != null && (p.isMoving() || p.isAnimating())) {
						lastAnimationTime = System.currentTimeMillis();
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
				}
			}
		});
		t.start();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int onLoop() throws InterruptedException {
		Player p = myPlayer();
		Inventory i = getInventory();
		
		if(p.isAnimating() || p.isMoving()) {
			lastAnimationTime = System.currentTimeMillis();
		}
		
		Action temp = nextAction();
		lastAction = currentAction;
		currentAction = temp;

		if(lastAction != currentAction)
			logger.debug("Executing Action: " + currentAction);

		switch (currentAction) {
			case FISH_SPOT:
				fish(FishingType.FLY);
				break;
			case CHOP_TREE:
				RS2Object tree = objects.closest(SBUIds.PLAIN_WOOD_TREES);
				if(tree != null) {
					if(i.isFull())
						i.interact(27, "Drop");
					
					tree.interact("Chop down");
				}
				break;
			case LIGHT_FIRE:
				if(i.contains("Tinderbox")) {
					if(i.isItemSelected())
						i.deselectItem();
					
					i.interact(i.getSlot("Tinderbox"), "Use");
					sleep(random(100, 500));
					i.interact("Use", "Logs");
				}
				break;
			case COOK_FOOD:
				RS2Widget cookWindow = getWidgets().get(270, 14);
				if(cookWindow != null && cookWindow.isVisible()) {
					cookWindow.interact("Cook All");
					Sleep.sleepUntil(() -> idleTime() > 3 || !i.contains(SBUFilters.UNCOOKED_FISH), 30000);
				}
				
				RS2Object fire = objects.closest("Fire");
				if(fire != null && fire.getPosition().distance(myPosition()) < 5) {
					i.interact(i.getSlot(SBUFilters.UNCOOKED_FISH), "Use");
					sleep(random(100, 500));
					fire.interact();
				}
			default:
				break;
		}
		
		return 2000;
	}
	
	void fish(FishingType type) {
		int fishingSpotId = 0;
		if(type == FishingType.SMALL_NET) fishingSpotId = NpcIds.NET_FISHING_SPOT;
		else if(type == FishingType.FLY) fishingSpotId = NpcIds.ROD_FISHING_SPOT;
		
		NPC fishingSpot = getNpcs().closest(fishingSpotId);
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
			
			Sleep.sleepUntil(() -> myPlayer().getAnimation() == 623, 10000);
		}
	}
	
	void dropFish() {
		Inventory inv = getInventory();
		logger.debug("Dropping fish");
		inv.dropAll(InventoryIds.BURNT_SHRIMP, InventoryIds.COOKED_SHRIMP, InventoryIds.RAW_SHRIMP);
		logger.debug("all fish dropped");
	}
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
		
		g.drawString("Idle: " + Formatting.msToReadable(idleTime()), 1, 40);
		g.drawString("Previous Action: " + lastAction, 1, 60);
		g.drawString("Current Action: " + currentAction, 1, 80);
	}
	
	public State getState() {
		return State.IDLE;
	}

	@SuppressWarnings("unchecked")
	public Action nextAction() {
		Player p = myPlayer();
		Inventory i = getInventory();
		
		if(i.isFull()) {
			if(i.contains(SBUFilters.LOG_FILTER)) {
				return Action.LIGHT_FIRE;
			}else {
				return Action.CHOP_TREE;
			}
		}
		
		if(i.isFull() || i.getEmptySlots() == 1) {
			if(objects.closest("Fire") != null) return Action.COOK_FOOD;
		}
		
		if(p.isAnimating() || p.isMoving() || idleTime() < 2) return Action.WAIT;
		
		return Action.FISH_SPOT;
	}
	
	public long idleTime() {
		return System.currentTimeMillis() - lastAnimationTime;
	}
	
	enum FishingType{
		SMALL_NET,
		BIG_NET,
		BAIT,
		FLY,
		HARPOON,
		CAGE
	}
	
}
