package com.skeeter144.script;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.script.MethodProvider;

import com.skeeter144.util.Rock;
import com.skeeter144.util.Util;

public class SBMiner extends SkeeterScript{

	public MiningType type = MiningType.COPPER_TIN;
	public MiningArea miningArea = MiningArea.VARROCK_EAST;
	Rock targetRockType = Rock.IRON;
	
	String minedOreName = "Iron ore";
	
	public SBMiner(MethodProvider m) {
		super("Skeeter's Miner", m);
	}

	@Override
	public State getState() {
		return currentState;
	}

	@Override
	public Action nextAction() {
		
		if(inv.isFull()) return Action.BANK_ITEMS;
		
		if(idleTime() < 2000) return currentAction;
		
		if(!miningArea.area.contains(player.getPosition())) return Action.TRAVEL;
		
		return Action.MINE_ORE;
	}
	

	@Override
	public int executeAction(Action action) throws InterruptedException {
		currentAction = nextAction();
	
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
		
		return 1000;
	}
	
	Area targetBank() {
		return Banks.VARROCK_EAST;
	}
	
	private Area getTravelDestination() {
		if(inv.isFull()) return targetBank();
		
		return MiningArea.VARROCK_EAST.area;
	}

	@SuppressWarnings("unchecked")
	private void mine() throws InterruptedException {
		if(idleTime() < 1000) return;
		
		RS2Object ore = script.getObjects().closest(obj -> targetRockType.hasOre(obj));;

		if(ore == null) {
			Util.log("Unable to find ore.  What up");
			return;
		}
		
		ore.interact("Mine");
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
