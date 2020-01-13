package com.skeeter144.script;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Bank.BankMode;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;

import com.skeeter144.sleep.Sleep;
import com.skeeter144.util.Util;

public class SBPizzaBaser extends SkeeterScript{

	final String FLOUR = "Pot of flour";
	final String WATER = "Bucket of water";
	final String PIZZA_BAE = "Pizza base";
	
	boolean haveFlour = false;
	boolean haveWater = false;
	
	public SBPizzaBaser(MethodProvider m) {
		super("Skeeter's Pizza Base Maker", m);
	}

	@Override
	public int onLoop() throws InterruptedException {
		Bank bank = script.getBank();
		Inventory inv = script.getInventory();
		
		Action a = nextAction();
		switch(a) {
			case OPEN_BANK:
				bank.open();
				break;
			case TAKE_ITEMS_FROM_BANK:
				takeSuppliesFromBank();
				break;
			case ASSEMBLE_ITEMS:
				assembleItems();
				break;
			case BANK_ITEMS:
				openBank();
				bank.depositAll();
				break;
			case SELL_ITEMS:
				sellProducts();
				break;
			case BUY_ITEMS:
				openBank();
				if(!inv.contains("Coins")) {
					bank.withdrawAll("Coins");	break;
				}
				buySupplies();
				break;
			case MAKE_TOMATO_PIZZAS:
				tomatoPizzas();
				break;
			case MAKE_CHEESE_PIZZAS:
				cheesePizzas();
				break;
			default:
				Util.log("Unimplemented action: " + a);
		}
		
		return 1000;
	}

	@Override
	public State getState() {
		return null;
	}

	@Override
	public Action nextAction() {
		Inventory inv = script.getInventory();
		Bank b = script.getBank();
		
		 if(inv.contains("Cheese") && inv.contains("Incomplete pizza")) {
			 return Action.MAKE_CHEESE_PIZZAS;
		 }
		 
		 if(inv.contains(FLOUR) && inv.contains(WATER)) 
				return Action.ASSEMBLE_ITEMS;
		
		 if(b.isOpen()) {
			 	haveFlour = b.contains(FLOUR);
				haveWater = b.contains(WATER);
				
				if(!b.contains(FLOUR) || !b.contains(WATER)) {
					 if(inv.contains("Tomato") && inv.contains("Pizza base")) {
						 return Action.MAKE_TOMATO_PIZZAS;
					 }
					 
					return Action.BUY_ITEMS;
				}
			}
		 
		 
		 
		if(inv.isEmpty() || inv.getEmptySlots() == 27 && inv.contains("Coins")) {
			if(!b.isOpen()) return Action.OPEN_BANK;
			
			if(b.contains(PIZZA_BAE) && b.contains("Tomato")) {
				return Action.MAKE_TOMATO_PIZZAS;
			}
			
			if(b.contains("Incomplete pizza") && b.contains("Cheese")) {
				return Action.MAKE_CHEESE_PIZZAS;
			}
			
			
			
			if(b.contains(FLOUR) && b.contains(WATER)) return Action.TAKE_ITEMS_FROM_BANK;
			else return Action.BUY_ITEMS;
		}
		
		return Action.TAKE_ITEMS_FROM_BANK;
	}
	
	void takeSuppliesFromBank() throws InterruptedException {
		Bank bank = script.getBank();
		
		if(bank.isOpen()) {		
			if(!haveFlour || !haveWater) return;
			
			if(!inv.contains(FLOUR)) {
				bank.withdraw(FLOUR, 9);
				Sleep.sleepUntil(() -> inv.contains(FLOUR), 1000);
			}
			
			if(!inv.contains(WATER)) {
				bank.withdraw(WATER, 9);
				Sleep.sleepUntil(() -> inv.contains(WATER), 1000);
			}
			
			if(inv.contains(WATER) && inv.contains(FLOUR))
				bank.close();
		}else {
			openBank();
		}
	}
	
	void cheesePizzas() throws InterruptedException {
		Inventory inv = script.getInventory();
		Bank b = script.getBank();

		if(!inv.contains("Cheese"))	takeItemFromBank("Cheese", 9);
		
		if(b.isOpen()) {b.close(); return;}
		
		inv.getItem("Incomplete pizza").interact("Use");
		sleep(random(500, 1000));
		inv.getItem("Cheese").interact("Use"); 
		
		Sleep.sleepUntil(() -> script.getWidgets().isVisible(270, 14), 3000);
		
		if (script.getWidgets().isVisible(270, 14)) {
			script.getWidgets().interact(270, 14, "Make");

			Sleep.sleepUntil(() -> !inv.contains("Cheese"), 10000);
		}
	}
	
	void tomatoPizzas() throws InterruptedException {
		Inventory inv = script.getInventory();
		Bank b = script.getBank();
		
		if(!inv.contains("Tomato"))	takeItemFromBank("Tomato", 9);
		if(!inv.contains("Pizza base"))	takeItemFromBank("Pizza base", 9);
		
		if(b.isOpen()) {b.close(); return;}
		
		inv.getItem("Tomato").interact("Use");
		sleep(random(500, 1000));
		inv.getItem("Pizza base").interact("Use"); 
		Sleep.sleepUntil(() -> script.getWidgets().isVisible(270, 14), 3000);
		
		if (script.getWidgets().isVisible(270, 14)) {
			script.getWidgets().interact(270, 14, "Make");

			Sleep.sleepUntil(() -> !inv.contains("Tomato"), 10000);
		}
	}
	
	void takeItemFromBank(String name, int amt) throws InterruptedException {
		Bank b = script.getBank();
		
		if(!inv.contains(name)) {
			openBank();
			if(!b.contains(name)) {
				return;
			}
			b.withdraw(name, amt);
			Sleep.sleepUntil(() -> 	inv.contains(name), 3000);
			return;
		}
	}

	void assembleItems() {
		if(script.getWidgets().isVisible(270)) {
			script.getWidgets().interact(270, 16, "Make");
			Sleep.sleepUntil(() -> !inv.contains(FLOUR), 10000);
			return;
		}
		
		if(!script.getTabs().isOpen(Tab.INVENTORY)) {
			script.getTabs().open(Tab.INVENTORY);
			return;
		}
		
		Item domItem = null;
		Item bitchItem = null;
		if(inv.isItemSelected()) 
		{
			String name = inv.getSelectedItemName();
			if(name.equals(FLOUR)) 
			{
				domItem = inv.getItemInSlot(inv.getSelectedItemIndex());
				bitchItem = inv.getItem(WATER);
			} 
			else if(name.equals(WATER)) 
			{
				domItem = inv.getItemInSlot(inv.getSelectedItemIndex());
				bitchItem = inv.getItem(WATER);
			}else {
				inv.deselectItem();
			}
			return;
		}
		
		
		if(domItem == null || bitchItem == null) {
			domItem = inv.getItem(FLOUR);
			bitchItem = inv.getItem(WATER);
			
			if(domItem == null || bitchItem == null) {
				script.logger.warn("Expected to find Bucket of water and Pot of flour.  Missing items.");
				return;
			}
		}
		
		
		script.getMouse().move(inv.getMouseDestination(inv.getSlot(domItem)));
		script.getMouse().click(false);
		
		Sleep.sleepUntil(() -> inv.isItemSelected(), 3000);
		
		script.getMouse().move(inv.getMouseDestination(inv.getSlot(bitchItem)));
		script.getMouse().click(false);
		
		Sleep.sleepUntil(() -> script.getWidgets().isVisible(270), 3000);
	}

	void buySupplies() throws InterruptedException {
		openGE();
		
		GrandExchange ge = script.getGrandExchange();
		if(script.getGrandExchange().isOpen()) {
			
			if(!haveFlour) {
				ge.buyItem(1933, "pot of", 130, 200);
				sleep(random(2000, 6000));
			}
			
			if(!haveWater) {
				ge.buyItem(1929, "et of wa", 30, 200);
				sleep(random(2000, 6000));
			}
			ge.collect(true);
		}
	}

	void sellProducts() throws InterruptedException {
		openBank();
		
		Bank b = script.getBank();
		if(!b.isBankModeEnabled(BankMode.WITHDRAW_NOTE)) {
			b.enableMode(BankMode.WITHDRAW_NOTE);
			return;
		}
		
		if(b.contains(PIZZA_BAE)) b.withdrawAll(PIZZA_BAE);
		else b.close();
		
		openGE();
		
		script.getInventory().interact("Offer", PIZZA_BAE);
		Sleep.sleepUntil(() -> script.getWidgets().isVisible(465, 24, 50), 3000);
		
		if( script.getWidgets().isVisible(465, 24, 50)) {
			script.getWidgets().interact(465, 24, 50, "-5%");
			sleep(1000);
			script.getWidgets().interact(465, 24, 6, "All");
			sleep(1000);
			script.getWidgets().interact(465, 27, "Confirm");
		}
		
	}
	
	void openBank() throws InterruptedException {
		Bank bank = script.getBank();
		if(!bank.isOpen()) { 
			bank.open(); 
			Sleep.sleepUntil(() -> bank.isOpen(), 5000); 
		}
	}
	
	void openGE() {
		NPC geClerk = script.getNpcs().closest("Grand Exchange Clerk");
		if(geClerk == null) { Util.log("Unable to find GE Clerk..."); return; }
		
		if(!isGEOpen()) {
			geClerk.interact("Exchange");
			Sleep.sleepUntil(() -> isGEOpen(), 5000);
			return;
		}
	}
	
	boolean isGEOpen() {
		return script.getWidgets().isVisible(465);
	}
}
