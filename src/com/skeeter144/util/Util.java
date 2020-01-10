package com.skeeter144.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.osbot.rs07.api.model.NPC;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skeeter144.main.MainScript;

public class Util {

	JsonObject jo;
	static boolean fileLoaded = false;
	public static void loadMonsterDrops(){
		
		try {
			String monstersContent = new String(Files.readAllBytes(Paths.get("/res", "monsters-complete.json")));
			MainScript.instance().logger.debug("File content: " + monstersContent.length());
		} catch (IOException e) {
			MainScript.instance().logger.debug(e.getMessage());
			e.printStackTrace();
		}
		
		JsonParser parser = new JsonParser();
		
	}
	
	public static List<String> lookupMonsterDrops(NPC monster){
		if(!fileLoaded) loadMonsterDrops();
		
		return null;
	}
	
}
