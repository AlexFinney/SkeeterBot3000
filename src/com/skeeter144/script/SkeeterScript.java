package com.skeeter144.script;

import java.awt.Graphics2D;

import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.utility.Logger;

public abstract class SkeeterScript{

	protected long lastAnimationTime = 0;
	protected State lastState = State.IDLE;
	protected State currentState = State.IDLE;
	protected Action currentAction = Action.NONE;
	protected MethodProvider script;
	protected Logger logger;
	public boolean running = false;
	public String name = "SkeeterScript";
	
	protected Player player;
	
	public SkeeterScript(MethodProvider m) {
		script = m;
    	logger = Logger.GLOBAL_LOGGER;
    	
	}
	
	@SuppressWarnings("unused")
	private SkeeterScript() {}
	
	public void onStart() {
		Thread t = new Thread(() -> {
			while(running) {
				
				
				
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public void onPaint(Graphics2D g) {}
	
	public long idleTime() {
		return System.currentTimeMillis() - lastAnimationTime;
	}
	
	public abstract int onLoop() throws InterruptedException;
	public abstract State getState();
	public abstract Action nextAction();
	
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
		ATTACK_MONSTER,
		PICK_UP_ITEMS,
		CHOP_TREE,
		FISH_SPOT,
		BANK_ITEMS,
		COOK_FOOD
	}
}
