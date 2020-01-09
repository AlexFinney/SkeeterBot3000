package com.skeeter144.main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.utility.Logger;

import com.skeeter144.sleep.Sleep;

@ScriptManifest(name = "GenericKiller", author = "Skeeer144", version = 1.0, info = "", logo = "") 
public class GenericKilllerScript extends Script {

	Logger loger;
	ConfigurationGui gui;
	
	boolean running = false;
    boolean buryBones = false;
    boolean buryingBones = false;
    boolean ironManMode = false;
    
    HashMap<Position, Long> forgetPositions = new HashMap<Position, Long>();
    HashMap<Position, Long> killPositions = new HashMap<Position, Long>();
	
	private List<String> targetItems = new ArrayList<String>();
	String targetName = "";
	
	@Override
    public void onStart() {
    	this.getBot().addMessageListener(this);
    	this.getBot().addKeyListener(new BotKeyListener() {
			@Override
			public void checkKeyEvent(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) gui.setVisible(!gui.isVisible());
			}
		});
    	
    	logger = Logger.GLOBAL_LOGGER;
    	gui = new ConfigurationGui(this);
    	gui.setVisible(true);
    }

    @Override
    public void onMessage(Message msg) throws InterruptedException {
    	super.onMessage(msg);
    	
    	if(msg.getMessage().contains("Iron Man")) {
    		forgetPositions.put(myPosition(), System.currentTimeMillis());
    	}
    }

    @Override

    public void onExit() {

        //Code here will execute after the script ends
    }

	
    @SuppressWarnings("unchecked")
	@Override
    public int onLoop() {
    	if(!running) return 1000;
    	
    	int dist = Integer.MAX_VALUE;
    	Entity closest = null;
    	Player player = myPlayer();
    	if(player.isUnderAttack())
    		return random(2000, 3000);
    	
    	if(inventory.isFull() && inventory.contains("Bones")) {
    		buryingBones = true;
    	}
    	
    	if(buryingBones && buryBones) {
    		if(inventory.contains("Bones")) {
    			inventory.interact("Bury", "Bones");
    			return random(900, 1200);
    		}else {
    			buryingBones = false;
    		}
    	}
    	
    	forgetPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
    	killPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
    	
		Entity target = getNpcs().closest(new Filter<NPC>() {
			public boolean match(NPC e) {
				return e.getName().contains(targetName) && !e.isUnderAttack() && e.isAttackable() && e.getHealthPercent() == 100;
			}
    	});
    	
    	GroundItem targetItem = getGroundItems().closest(new Filter<GroundItem>() {
			public boolean match(GroundItem i) {
				return targetItems.contains(i.getName()) 
						&& (!ironManMode ||	!forgetPositions.containsKey(i.getPosition())
											&& killPositions.containsKey(i.getPosition()));
			}
    	});
    	
    	if(!inventory.isFull() && targetItem != null && targetItem.getPosition().distance(myPosition()) < 5)
    	{
    		targetItem.interact("Take");
    		Sleep.sleepUntil(() -> !targetItem.exists(), 5000);
    		return random(500, 1200);
    	}
    	
    	if (target != null && target.interact("Attack")) {
			Sleep.sleepUntil(() -> ((NPC)target).getHealthPercent() == 0, 15000);
			
			if(((NPC)target).getHealthPercent() == 0 || !target.exists())
				killPositions.put(target.getPosition(), System.currentTimeMillis());
			
			return random(500, 2000);
		}
    	
//    	if(closest == bones) {
//    		if(bones.interact("Take")) {
//    			Sleep.sleepUntil(() -> !player.isMoving() || !bones.exists(), 5000);
//    			return random(50, 150);
//    		}
//    	}
    	
    	return 1000;
    }

    @Override
    public void onPaint(Graphics2D g) {

        //This is where you will put your code for paint(s)
    }
    
    public void setTargetItems(String str) {
    	targetItems = Arrays.asList(str.split(" "));
    }
    
    public static void main(String[] args) {}
}


