package com.skeeter144.script;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.Script;

import com.skeeter144.gui.SBKillerGui;
import com.skeeter144.main.MainScript;
import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Util;

public class SBKiller extends SkeeterScript implements MessageListener {

	public SBKiller(Script script) {
		super("Skeeter's Killer + Loot 'n Bones", script);
	}
	
	public boolean running = false;
    public boolean buryBones = false;
    boolean buryingBones = false;
    public boolean ironManMode = false;
    public boolean lootItems = true;
    
    NPC currentTarget = null;
    
    HashMap<Position, Long> forgetPositions = new HashMap<Position, Long>();
    HashMap<Position, Long> killPositions = new HashMap<Position, Long>();
	
	private List<String> targetItems = new ArrayList<String>();
	public String targetName = "";
	
	@Override
    public void onStart() {
    	script.getBot().addMessageListener(this);
    	script.getBot().addKeyListener(new BotKeyListener() {
			@Override
			public void checkKeyEvent(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) gui.setVisible(!gui.isVisible());
			}
		});
    	
    	gui = new SBKillerGui(this);
    	gui.setVisible(true);
    }

	@Override
	public Action nextAction() {
		if(!isRunning()) return Action.NONE;

    	Player player = script.myPlayer();
    	Inventory inv = script.getInventory();
		
    	if(player.isUnderAttack()) return Action.ATTACK_TARGET;
    	
    	if(buryBones && (buryingBones || inv.isFull()) && inv.contains("Bones")) return Action.BURY_BONES;
    	
    	if(lootItems && !targetItems.isEmpty()) {
	    	GroundItem targetItem = getTargetItem();
	    	if(!inv.isFull() && targetItem != null && targetItem.getPosition().distance(player.getPosition()) < 5) {
	    		return Action.PICK_UP_ITEMS;
	    	}
    	}
    	
    	NPC target = getTarget();
    	if (target != null) {
    		currentTarget = target;
    		return Action.ATTACK_TARGET;
    	}
    	
		return Action.NONE;
	}
    
    void buryBones() {
    	Inventory inv = script.getInventory();
    	if(!script.getTabs().isOpen(Tab.INVENTORY)) {
    		script.getTabs().open(Tab.INVENTORY);
    		return;
    	}
    	
    	if(inv.isFull() && inv.contains("Bones")) {
    		buryingBones = true;
    	}
    	
    	if(buryingBones && buryBones) {
    		if(inv.contains("Bones")) {
    			inv.interact("Bury", "Bones");
    		}else {
    			buryingBones = false;
    		}
    	}
    }

    @SuppressWarnings("unchecked")
    NPC getTarget() {
    	return script.getNpcs().closest(new Filter<NPC>() {
			public boolean match(NPC e) {
				return e.getName().contains(targetName) && !e.isUnderAttack() && e.isAttackable() && e.getHealthPercent() == 100;
			}
    	});
    }
    
    @SuppressWarnings("unchecked")
    GroundItem getTargetItem() {
    	if(targetItems.isEmpty()) return null;
    	
    	return script.getGroundItems().closest(new Filter<GroundItem>() {
			public boolean match(GroundItem i) {
				return targetItems.contains(i.getName()) 
						&& (!MainScript.instance().ironManMode || !forgetPositions.containsKey(i.getPosition())
											&& killPositions.containsKey(i.getPosition()));
			}
    	});
    }
    
    @Override
    public void onPaint(Graphics2D g) {

        //This is where you will put your code for paint(s)
    }
    
    public void setTargetItems(String str) {
    	targetItems = Arrays.asList(str.split(" "));
    }
    
    public void setTargetItems(List<String> str) {
    	targetItems = str;
    }

	public boolean isRunning() {
		return running;
	}

	@Override
	public State getState() {
		return currentState;
	}
	
    @Override
    public void onMessage(Message msg) throws InterruptedException {
    	if(msg.getMessage().contains("Iron Man")) {
    		forgetPositions.put(script.myPosition(), System.currentTimeMillis());
    	}
    }

	@Override
	public int executeAction(Action action) {
		if(!isRunning()) return 1000;
    	
    	Inventory inv = script.getInventory();
    	
    	if(ironManMode) {
	    	forgetPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
	    	killPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
    	}
    	
    	currentAction = nextAction();
    	
    	switch(currentAction) {
    		case BURY_BONES:
    			buryBones();
    			break;
    		case PICK_UP_ITEMS:
    			GroundItem targetItem = getTargetItem();
    			if(!inv.isFull() && targetItem != null && targetItem.interact("Take"))
    				Sleep.sleepUntil(() -> !targetItem.exists(), 5000);
    			break;
    		case ATTACK_TARGET:
    			if(isPlayerUnderAttack() || idleTime() < 3000) break;
    			
    			NPC target = getTarget();
    			if (target != null && target.interact("Attack")) {
    				Sleep.sleepUntil(() -> ((NPC)target).getHealthPercent() == 0, 15000);
    				
    				if(((NPC)target).getHealthPercent() == 0 || !target.exists()) {
    					if(ironManMode) killPositions.put(target.getPosition(), System.currentTimeMillis());
    					currentTarget  = null;
    				}
    			}
    			break;
    		case NONE:
    			break;
    		default:
    			Util.log("Unimplemented action: " + currentAction);
    	}
    	
    	return 1000;
	}
}