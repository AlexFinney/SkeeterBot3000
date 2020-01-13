package com.skeeter144.script;

import java.util.HashMap;
import java.util.Map;

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
	final String CHEESE = "Cheese";
	final String TOMATO = "Tomato";
	final String INCOMPLETE_PIZZA = "Incomplete pizza";
	final String UNCOOKED_PIZZA = "Uncooked pizza";
	
	Map<String, Long> bankItemStock = new HashMap<>();
	
	public SBPizzaBaser(MethodProvider m) {
		super("Skeeter's Pizza Base Maker", m);
	}

	@Override
	public int onLoop() throws InterruptedException {
		Bank bank = script.getBank();
		//Inventory inv = script.getInventory();
		
		Action a = nextAction();
		switch(a) {
			case OPEN_BANK:
				openBank();
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
				//sellProducts();
				break;
			case BUY_ITEMS:
//				openBank();
//				if(!inv.contains("Coins")) {
//					bank.withdrawAll("Coins");	
//					break;
//				}
//				buySupplies();
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
		Action action = Action.NONE;
		
		// if inv not empty, analyze to see what we can craft
		if (!inv.isEmpty()) {
			action = analyzeInventory();
			if (action != Action.NONE) return action;
		}

		// if empty inventory and bank is not open, need to open it to do something
		if(inv.isEmpty() && !b.isOpen()) return Action.OPEN_BANK;
		
		// --- bank is open --- 
		action = analyzeBank();
		return action;
	}
	
	Action analyzeInventory() {
		Inventory inv = script.getInventory();
		
		if(inv.contains(PIZZA_BAE) && inv.contains(TOMATO)) 
			return Action.ASSEMBLE_ITEMS;
		
		
		 if(inv.contains(CHEESE) && inv.contains(INCOMPLETE_PIZZA)) 
			 return Action.ASSEMBLE_ITEMS;
		 
		 
		 if(inv.contains(FLOUR) && inv.contains(WATER)) 
			return Action.ASSEMBLE_ITEMS;
		 
		 if(inv.contains("Pot") && !inv.contains(FLOUR)) {
			 return Action.BANK_ITEMS;
		 }
		 
		 // we just made these, so bank them
		 if(inv.contains(INCOMPLETE_PIZZA) && !inv.contains(TOMATO) ||
		    inv.contains(UNCOOKED_PIZZA) && !inv.contains(CHEESE)) 
		 {
			 return Action.BANK_ITEMS;
		 }
		 
		 
		 
		return Action.NONE;
	}
	
	Action analyzeBank() {
		Bank bank = script.getBank();
		
		if(!bank.isOpen()) return Action.OPEN_BANK;
		
		if(shouldTakeItemForCrafting(FLOUR, WATER) || shouldTakeItemForCrafting(WATER, FLOUR)) { 
			return Action.TAKE_ITEMS_FROM_BANK;
		}
		
		if(shouldTakeItemForCrafting(TOMATO, PIZZA_BAE) || shouldTakeItemForCrafting(PIZZA_BAE, TOMATO)) { 
			return Action.TAKE_ITEMS_FROM_BANK;
		}
	
		if(shouldTakeItemForCrafting(CHEESE, INCOMPLETE_PIZZA) || shouldTakeItemForCrafting(INCOMPLETE_PIZZA, CHEESE)) { 
			return Action.TAKE_ITEMS_FROM_BANK;
		}
		
		return Action.NONE;
	}
	
	boolean shouldTakeItemForCrafting(String name, String partnerComp) {
		Inventory inv = script.getInventory();
		Bank bank = script.getBank();
		
		return !inv.contains(name) && bank.contains(name) && (bank.contains(partnerComp) || inv.contains(partnerComp));
	}
	
	void takeSuppliesFromBank() throws InterruptedException {
		Bank bank = script.getBank();
		Inventory inv = script.getInventory();
		
		if (!openBank())
			return;
		
		// if there's flour and water available, take them
		if ((bank.contains(FLOUR) || inv.contains(FLOUR)) && (bank.contains(WATER) || inv.contains(WATER))) {
			if (!inv.contains(FLOUR)) {
				bank.withdraw(FLOUR, 9);
				Sleep.sleepUntil(() -> inv.contains(FLOUR), 1000);
				return;
			}

			if (!inv.contains(WATER) && bank.contains(WATER)) {
				bank.withdraw(WATER, 9);
				Sleep.sleepUntil(() -> inv.contains(WATER), 1000);
				return;
			}
		}

		// if there's tomato and pizza bae available, take them
		if ((bank.contains(TOMATO) || inv.contains(TOMATO)) && (bank.contains(PIZZA_BAE) || inv.contains(PIZZA_BAE))) {
			if (!inv.contains(TOMATO)) {
				bank.withdraw(TOMATO, 14);
				Sleep.sleepUntil(() -> inv.contains(TOMATO), 1000);
				return;
			}

			if (!inv.contains(PIZZA_BAE)) {
				bank.withdraw(PIZZA_BAE, 14);
				Sleep.sleepUntil(() -> inv.contains(PIZZA_BAE), 1000);
				return;
			}
		}

		// if there's cheese and incomplete pizza in the bank, take them
		if ((bank.contains(CHEESE) || inv.contains(CHEESE)) && (bank.contains(INCOMPLETE_PIZZA) || inv.contains(INCOMPLETE_PIZZA))) {
			if(!inv.contains(CHEESE)) {
				bank.withdraw(CHEESE, 14);
				Sleep.sleepUntil(() -> inv.contains(CHEESE), 1000);
				return;
			}
			
			if (!inv.contains(INCOMPLETE_PIZZA)) {
				bank.withdraw(INCOMPLETE_PIZZA, 14);
				Sleep.sleepUntil(() -> inv.contains(INCOMPLETE_PIZZA), 1000);
				return;
			}
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
		Inventory inv = script.getInventory();
		
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
		if(script.getBank().isOpen()) {
			script.getBank().close();
		}
		
		Inventory inv = script.getInventory();
		
		Item domItem = null;
		Item bitchItem = null;
		if((domItem = inv.getItem(WATER)) != null && (bitchItem = inv.getItem(FLOUR)) != null) {
			assembleAll(inv, domItem, bitchItem, 270, 16);
			return;
		}
		
		if((domItem = inv.getItem(PIZZA_BAE)) != null && (bitchItem = inv.getItem(TOMATO)) != null) {
			assembleAll(inv, domItem, bitchItem, 270, 14);
			return;
		}
		
		if((domItem = inv.getItem(INCOMPLETE_PIZZA)) != null && (bitchItem = inv.getItem(CHEESE)) != null) {
			assembleAll(inv, domItem, bitchItem, 270, 14);
			return;
		}
	}

	void assembleAll(Inventory inv, Item dom, Item bitch, int parentWidget, int subWidget) {
		if(script.getWidgets().isVisible(parentWidget)) {
			if(script.getWidgets().isVisible(parentWidget, subWidget)) script.getWidgets().interact(parentWidget, subWidget, "Make"); // pizza base widget make all
			
			Sleep.sleepUntil(() -> !inv.contains(dom.getName()) || !inv.contains(bitch.getName()), 20000);
			return;
		}
		
		if(!script.getTabs().isOpen(Tab.INVENTORY)) {
			script.getTabs().open(Tab.INVENTORY);
			return;
		}
		
		if(inv.isItemSelected() && !inv.getSelectedItemName().equalsIgnoreCase(dom.getName())){
			inv.deselectItem();
			return;
		}
		
		if(!inv.isItemSelected()) {
			Item i = inv.getItem(dom.getName());
			if(i != null) {
				script.getMouse().move(inv.getMouseDestination(inv.getSlot(i)));
				script.getMouse().click(false);
			}
			return;
		}
		
		if(inv.isItemSelected() && inv.getSelectedItemName().equalsIgnoreCase(dom.getName())) {
			Item i = inv.getItem(bitch.getName());
			if(i != null) i.interact("Use");
			return;
		}
	}
	
	void buySupplies() throws InterruptedException {
		openGE();
		
		GrandExchange ge = script.getGrandExchange();
		if(script.getGrandExchange().isOpen()) {
			
//			if(!haveFlour) {
//				ge.buyItem(1933, "pot of", 130, 200);
//				sleep(random(2000, 6000));
//			}
//			
//			if(!haveWater) {
//				ge.buyItem(1929, "et of wa", 30, 200);
//				sleep(random(2000, 6000));
//			}
			ge.collect(true);
		}
	}

	void sellProducts() throws InterruptedException {
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
