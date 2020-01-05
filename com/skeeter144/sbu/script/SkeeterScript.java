package com.skeeter144.script;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;

import com.skeeter144.misc.Formatting;

public abstract class SkeeterScript extends Script{

	protected State lastState = State.IDLE;
	protected Action nextAction = Action.NONE;
	public abstract State getState();
	protected long startTime = 0;
	
	protected boolean showStats = true;
	
	protected Map<Skill, Integer> xpEarned = new HashMap<Skill, Integer>();
	long lastXpUpdate = -1;
	
	@Override
	public void onStart() throws InterruptedException {
		super.onStart();
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
		
		if(System.currentTimeMillis() - lastXpUpdate > 1000) updateXpEarned();
		
		if(showStats) {
			g.drawString(runningTimeFormatted(), 1, 1);
			
			int line = 0;
			for(Map.Entry<Skill, Integer> entry : xpEarned.entrySet()) {
				if(entry.getValue() > 0) {
					g.drawString(entry.getKey().name() + "XP: " + entry.getValue(), 1, 10 * line);
					++line;
				}
			}
		}
	}
	
	protected void updateXpEarned() {
		Skill[] skills = Skill.values();
		for(Skill s : skills)
			xpEarned.put(s, experienceTracker.getGainedXP(s));
	}
	
	public long runningTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public String runningTimeFormatted() {
		return Formatting.msToReadable(runningTime());
	}
	
	public abstract Action nextAction();
	
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
