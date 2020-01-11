package com.skeeter144.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.osbot.rs07.api.model.NPC;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skeeter144.util.Util;

public class MonsterData {
	
	private static JsonObject monsters = null;
	
	public static List<String> lookupMonsterDrops(NPC monster){
		if(monsters == null) return new ArrayList<>();
		
		List<String> monsterDrops = new ArrayList<String>();
		JsonElement monsterElement = monsters.get(Integer.toString(monster.getId()));
		if (monsterElement != null && monsterElement.isJsonObject()) {
			JsonObject dropsObject = monsterElement.getAsJsonObject();
			JsonArray drops = dropsObject.get("drops").getAsJsonArray();
			drops.forEach((drop) -> {
				String dropName = drop.getAsJsonObject().get("name").getAsString();
				boolean membersItem = drop.getAsJsonObject().get("members").getAsBoolean();

				if (membersItem)
					dropName += "*";
				
				monsterDrops.add(dropName);
			});
		}
		
		boolean allMembers = true;
		for(String s : monsterDrops) {
			if(!s.endsWith("*")) allMembers = false;
			if(!allMembers) break;
		}
		
		if(allMembers) {
			List<String> temp = new ArrayList<String>();
			for(String s : monsterDrops) {
				temp.add(s.substring(0, s.length() - 2));
			}
		}
		
		return monsterDrops;
	}
	
	
	public static void loadMonsterDrops(){ loadMonsterDrops(null); }
	
	public static void loadMonsterDrops(Runnable onDone){
		Thread t = new Thread(() ->  {
			try {
				String json = downloadMonstersFile();
				if(json.isEmpty()) return;
				
				JsonParser parser = new JsonParser();
				monsters = parser.parse(json).getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(onDone != null) onDone.run();	
		});
		t.start();
	}
	
	private static String downloadMonstersFile() throws IOException {
		File monstersFile = new File(Util.OSBotDataDir() + "/" + "monsters-complete.json");
		if(monstersFile.exists()) return "";
		
		String url = "https://www.osrsbox.com/osrsbox-db/monsters-complete.json";
		FileWriter fw = new FileWriter(monstersFile);
		
		InputStream is = new URL(url).openStream();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}

			fw.write(sb.toString());
		} finally {
			is.close();
			fw.close();
		}

		return sb.toString();
	}
	
	
}
