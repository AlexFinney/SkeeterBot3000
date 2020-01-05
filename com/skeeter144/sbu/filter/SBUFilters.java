package com.skeeter144.filter;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;

public class SBUFilters {

	public static final Filter<Item> LOG_FILTER = new Filter<Item>() {
		public boolean match(Item i) {
			return i.getName().contains("Log");
		}
	};
	
	public static final Filter<Item> UNCOOKED_FISH = new Filter<Item>() {
		public boolean match(Item i) {
			return i.getName().contains("Raw");
		}
	};
	
}
