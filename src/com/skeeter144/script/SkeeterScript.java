package com.skeeter144.script;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.utility.Logger;

import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Util;

public abstract class SkeeterScript{

	private long lastAnimationTime = 0;
	protected State lastState = State.IDLE;
	protected State currentState = State.IDLE;
	protected Action currentAction = Action.NONE;
	protected Action lastAction = Action.NONE;
	protected Script script;
	protected Logger logger;
	public boolean running = false;
	public String name;
	
	protected Player player;
	protected Inventory inv;
	protected Bank bank;
	protected Position myPos;
	
	public JFrame gui;
	
	public SkeeterScript(String name, Script m) {
		script = m;
    	logger = Logger.GLOBAL_LOGGER;
    	this.name = name;
	}
	
	@SuppressWarnings("unused")
	private SkeeterScript() {}
	
	public void onStart() {
		Thread t = new Thread(() -> {
			int lastRotation = -1;
			while(true) {
				try {
					if(script == null || player == null) {
						Thread.sleep(500);
						continue;
					}
					
					if(player.isMoving() || player.isAnimating() 
							|| lastRotation != player.getRotation()) 
					{
						lastAnimationTime = System.currentTimeMillis();
					}
					
					lastRotation = player.getRotation();
					
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
					Util.log(e.getLocalizedMessage());
				}
			}
		});
		t.start();
		
	}
	
	public void onPaint(Graphics2D g) {}
	
	public long idleTime() {
		Util.log((System.currentTimeMillis() - lastAnimationTime) + "");
		return System.currentTimeMillis() - lastAnimationTime;
	}
	
	public final int onLoop() throws InterruptedException{
		player = script.myPlayer();
		inv = script.getInventory();
		myPos = script.myPosition();
		bank = script.getBank();
		
		lastAction = currentAction;
		currentAction = nextAction();
		
		int actionSleep = executeAction(currentAction);
		
		return actionSleep > 0 ? actionSleep : 1000;
		
	}
	
	public abstract State getState();
	public abstract Action nextAction();
	public abstract int executeAction(Action action) throws InterruptedException;
	
	public int random(int min, int max) {
		return MethodProvider.random(min, max);
	}
	
	public void sleep(int millis) throws InterruptedException {
		MethodProvider.sleep(millis);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public MethodProvider getMethodProvider() {
		return script;
	}
	
	public void setGuiVisible(boolean visible) {
		if(gui != null) gui.setVisible(visible);
	}
	
	boolean openBank() throws InterruptedException {
		Bank bank = script.getBank();
		boolean success = true;
		if(!bank.isOpen()) { 
			success = bank.open(); 
			Sleep.sleepUntil(() -> bank.isOpen(), 5000);
		}
		return success;
	}
	
	long lastAttackedTime = 0;
	boolean isPlayerUnderAttack() {
		if(script.myPlayer().isUnderAttack()) {
			lastAttackedTime = System.currentTimeMillis();
		}
		
		return (System.currentTimeMillis() - lastAttackedTime) < 3000;
	}
	
	public enum State{
		IDLE,
		MINING,
		FISHING,
		FIGHTING,
		COOKING,
		CHOPPING,
		MOVING,
		SMITHING,
		SMELTING,
		RUNECRAFTING,
		TALKING,
		INTERACTING,
		FINISHED
	}
	
	public enum Action{
		NONE,
		WAIT,
		LIGHT_FIRE,
		DROP_ITEMS,
		MINE_ORE,
		INTERACT_FURNACE,
		INTERACT_ANVIL,
		ATTACK_TARGET,
		PICK_UP_ITEMS,
		CHOP_TREE,
		FISH_SPOT,
		ASSEMBLE_ITEMS,
		BANK_ITEMS,
		TAKE_ITEMS_FROM_BANK,
		OPEN_BANK,
		COOK_FOOD,
		BURY_BONES,
		BUY_ITEMS,
		SELL_ITEMS,
		TRAVEL
	}
}
