package com.skeeter144.script;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.Script;

import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Rock;
import com.skeeter144.util.Util;

public class SBMiner extends SkeeterScript{

	public MiningType type = MiningType.COPPER_TIN;
	public MiningArea miningArea = MiningArea.VARROCK_EAST;
	Rock targetRockType = Rock.IRON;
	
	String minedOreName = "Iron ore";
	
	HashMap<Position, Boolean> veinPositions = new HashMap<>();
	Position targetOrePos = null;
	
	public SBMiner(Script script) {
		super("Skeeter's Miner", script);
	}

	@Override
	public Action nextAction() {
		
		if(inv.isFull()) return Action.BANK_ITEMS;
		
		if(idleTime() < 1000) return currentAction;
		
		if(!miningArea.area.contains(player.getPosition())) return Action.TRAVEL;
		
		return Action.MINE_ORE;
	}
	

	@Override
	public int executeAction(Action action) throws InterruptedException {
		currentAction = nextAction();
	
		examineRocks();
		
		switch (currentAction) {
			case TRAVEL:
				Area dest = getTravelDestination();
				if(!dest.contains(myPos)) {
					WebWalkEvent evt = new WebWalkEvent(dest);
					evt.setMoveCameraDuringWalking(false);
					script.execute(evt);
					break;
				}
				break;
			case BANK_ITEMS:
				if(idleTime() < 1000) break;
				
				
				Area a = Banks.VARROCK_EAST;
				if(!a.contains(script.myPosition())) {
					WebWalkEvent evt = new WebWalkEvent(a);
					evt.setEnergyThreshold(101);
					evt.setMoveCameraDuringWalking(false);
					script.execute(evt);
					break;
				}
				
				if(bank == null) {
					Util.log("Cant find bank waddup");
					break;
				}
				
				if(!bank.isOpen()) {
					openBank();
					break;
				}
				
				if(inv.contains(minedOreName)) {
					bank.depositAll(minedOreName);
				}
				
				break;
			case MINE_ORE:
				mine();
				break;
			default:
				break;
		}
		
		return 500;
	}
	
	@SuppressWarnings("unchecked")
	private void mine() throws InterruptedException {
		examineRocks();
		
		if(targetOrePos != null && veinPositions.get(targetOrePos) && idleTime() < 1000) return; 
		
		
		RS2Object ore = script.getObjects().closest(obj -> targetRockType.hasOre(obj));

		if(ore == null) {
			Util.log("Unable to find ore.  What up");
			return;
		}
		
		targetOrePos = ore.getPosition();
		ore.interact("Mine");
		Sleep.sleepUntil(() -> player.isMoving(), 2000);
	}
	
	@SuppressWarnings("unchecked")
	private void examineRocks() {
		
		List<Position> orePositions = new ArrayList<>();
		script.getObjects().filter(obj -> targetRockType.hasOre(obj)).forEach((x) -> orePositions.add(x.getPosition()));
		
		
		for(Position pos : orePositions) {
			veinPositions.put(pos, true);
		}
		
		for(Entry<Position, Boolean> entry : veinPositions.entrySet()) {
			if(!orePositions.contains(entry.getKey()))
				veinPositions.put(entry.getKey(), false);
		}
		
	}
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);

		for (Entry<Position, Boolean> entry : veinPositions.entrySet()) {
			
			Util.log(script.myPosition() + " - " + entry.getKey());
			if(entry.getValue()) 
				Util.drawTile(script, g, entry.getKey(), Color.GREEN, Color.WHITE,  Util.walkingDistToPosition(script, entry.getKey()) + "");
		}
		
		if(targetOrePos != null)
			Util.drawTile(script, g, targetOrePos, Color.BLUE);
	}

	Area targetBank() {
		return Banks.VARROCK_EAST;
	}
	
	private Area getTravelDestination() {
		if(inv.isFull()) return targetBank();
		
		return MiningArea.VARROCK_EAST.area;
	}
	
	@Override
	public State getState() {
		return currentState;
	}

	
	public enum MiningArea{
		VARROCK_EAST(new Area(3281, 3361, 3289, 3370)),
		VARROCK_WEST(new Area(0, 0, 0, 0));

		public Area area;
		
		MiningArea(Area area) {
			this.area = area;
		}
	}
	
	public enum MiningType{
		COPPER_TIN,
		IRON,
		COAL,
		SILVER,
		GOLD,
		MITHRIL,
		ADAMANTITE,
		RUNITE
	}

}
