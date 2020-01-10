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
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.MethodProvider;

import com.skeeter144.gui.SBKillerGui;
import com.skeeter144.main.MainScript;
import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Util;

public class SBKiller extends SkeeterScript implements MessageListener {

	public SBKiller(MethodProvider m) {
		super(m);
		name = "Skeeter's Killer + Loot 'n Bones"; 
		
		Util.loadMonsterDrops();
	}

	SBKillerGui gui;
	
	public boolean running = false;
    public boolean buryBones = false;
    boolean buryingBones = false;
    
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
    public void onMessage(Message msg) throws InterruptedException {
    	if(msg.getMessage().contains("Iron Man")) {
    		forgetPositions.put(script.myPosition(), System.currentTimeMillis());
    	}
    }

    @SuppressWarnings("unchecked")
	@Override
    public int onLoop() {
    	if(!isRunning()) return 1000;
    	
    	Player player = script.myPlayer();
    	Inventory inventory = script.inventory;
    	
    	if(player.isUnderAttack())
    		return random(2000, 3000);
    	
    	
    	int sleep = 0;
    	if((sleep = buryBones()) > 0) return sleep;
    	
    	
    	forgetPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
    	killPositions.entrySet().removeIf((i) -> System.currentTimeMillis() - i.getValue() > 60 * 2 * 1000);
    	
		Entity target = script.getNpcs().closest(new Filter<NPC>() {
			public boolean match(NPC e) {
				return e.getName().contains(targetName) && !e.isUnderAttack() && e.isAttackable() && e.getHealthPercent() == 100;
			}
    	});
    	
    	GroundItem targetItem = script.getGroundItems().closest(new Filter<GroundItem>() {
			public boolean match(GroundItem i) {
				return targetItems.contains(i.getName()) 
						&& (!MainScript.instance().ironManMode || !forgetPositions.containsKey(i.getPosition())
											&& killPositions.containsKey(i.getPosition()));
			}
    	});
    	
    	if(!inventory.isFull() && targetItem != null && targetItem.getPosition().distance(player.getPosition()) < 5)
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
    
    private int buryBones() {
    	if(inv.isFull() && inv.contains("Bones")) {
    		buryingBones = true;
    	}
    	
    	if(buryingBones && buryBones) {
    		if(inv.contains("Bones")) {
    			inv.interact("Bury", "Bones");
    			return random(900, 1200);
    		}else {
    			buryingBones = false;
    		}
    	}
    	return 0;
    }

    @Override
    public void onPaint(Graphics2D g) {

        //This is where you will put your code for paint(s)
    }
    
    public void setTargetItems(String str) {
    	targetItems = Arrays.asList(str.split(" "));
    }

	public boolean isRunning() {
		return running;
	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action nextAction() {
		// TODO Auto-generated method stub
		return null;
	}
}


