package com.skeeter144.script;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;

import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Util;

public class SBCooker_AlKharid extends SkeeterScript{

	boolean initialized = false;
	
	public String cookItem = "Uncooked pizza";
	
	public SBCooker_AlKharid(MethodProvider m) {
		super("Skeeter's Al Kharid Cooker", m);
	}


	@Override
	public State getState() {
		return null;
	}

	@Override
	public Action nextAction() {
		Inventory inv = script.getInventory();
		Bank bank = script.getBank();
		
		if(!initialized) {
			if(Banks.AL_KHARID.getCentralPosition().distance(script.myPosition()) > 30) {
				return Action.TRAVEL;
			}else {
				initialized = true;
			}
		}
		
		if(!inv.contains(cookItem) && !inv.isEmpty())
			return Action.BANK_ITEMS;
		
		if(inv.isEmpty()) {
			if(	!bank.isOpen()) return Action.OPEN_BANK;
			else {
				if(!bank.contains(cookItem)) return Action.TRAVEL;
				return Action.TAKE_ITEMS_FROM_BANK;
			}
		}
			 
		if(inv.contains(cookItem)) return Action.COOK_FOOD;
		
		return Action.NONE;
	}

	void getFoodFromBank() throws InterruptedException {
		Bank bank = script.getBank();
		Inventory inv = script.getInventory();
		
		if (!openBank())
			return;
		
		if(!inv.isFull() && bank.contains(cookItem)) {
			bank.withdraw(cookItem, 28);
		}
	}
	
	void cookFood() {
		Bank bank = script.getBank();
		Inventory inv = script.getInventory();
		
		Util.log("asdasdasd");
		
		if(bank.isOpen()) {
			bank.close();
			return;
		}
		
		if(script.getWidgets().isVisible(270, 14)) {
			script.getWidgets().interact(270, 14, "Cook"); // pizza base widget make all
			
			Sleep.sleepUntil(() -> !script.getWidgets().isVisible(270, 14) && idleTime() > 4000, 30000);
			return;
		}
		
		if(!inv.contains(cookItem)) {
			Util.log("Expected to have " + cookItem + " in inventory but item is missing.");
			return;
		}
		
		RS2Object range = script.getObjects().closest("Range");
		if(range == null || !range.isVisible()) {
			Util.log("walk");
			script.getWalking().webWalk(new Position(3272, 3180, 0));
			return;
		}else {
			if(idleTime() > 1000) {
				Util.log("cook");
				if(!range.interact("Cook")) {
					script.getWalking().webWalk(new Position(3272, 3180, 0));
				}
				return;
			}
		}
	}


	@Override
	public int executeAction(Action action) throws InterruptedException {
		Action a = nextAction();
		Bank bank = script.getBank();
		
		switch(a) {
			case TRAVEL:
				if(bank.isOpen() && !bank.contains(cookItem)) script.getWalking().webWalk(Banks.GRAND_EXCHANGE);
				if(idleTime() > 5000) script.getWalking().webWalk(Banks.AL_KHARID);
				break;
			case OPEN_BANK:
				openBank();
				break;
			case BANK_ITEMS:
				openBank();
				bank.depositAll();
				break;
			case TAKE_ITEMS_FROM_BANK:
				getFoodFromBank();
				break;
			case COOK_FOOD:
				cookFood();
				break;
			case NONE: 
				break;
			default:
				Util.log("Unimplemented action: " + a);
		}
		
		return 1000;
	}
	
}
