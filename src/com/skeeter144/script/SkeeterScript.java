package com.skeeter144.script;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.utility.Logger;

import com.skeeter144.sleep.Sleep;

public abstract class SkeeterScript{

	private long lastAnimationTime = 0;
	protected State lastState = State.IDLE;
	protected State currentState = State.IDLE;
	protected Action currentAction = Action.NONE;
	protected MethodProvider script;
	protected Logger logger;
	public boolean running = false;
	public String name;
	
	public JFrame gui;
	
	public SkeeterScript(String name, MethodProvider m) {
		script = m;
    	logger = Logger.GLOBAL_LOGGER;
    	this.name = name;
	}
	
	@SuppressWarnings("unused")
	private SkeeterScript() {}
	
	public void onStart() {
		Thread t = new Thread(() -> {
			while(running) {
				if(script.myPlayer().isMoving() || script.myPlayer().isAnimating()) 
					lastAnimationTime = System.currentTimeMillis();
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
		ASSEMBLE_ITEMS,
		BANK_ITEMS,
		TAKE_ITEMS_FROM_BANK,
		OPEN_BANK,
		COOK_FOOD,
		BUY_ITEMS,
		SELL_ITEMS,
		TRAVEL
	}
}
