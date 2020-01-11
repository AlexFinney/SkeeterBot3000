package com.skeeter144.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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
	public static void loadMonsterDrops(Runnable onDone){
		try {
			downloadFile();
			JsonParser parser = new JsonParser();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> lookupMonsterDrops(NPC monster){
		//if(!fileLoaded) loadMonsterDrops();
		
		return null;
	}
	
	private static void downloadFile() throws IOException {
		File monstersFile = new File(OSBotDataDir() + "/" + "monsters-complete.json");
		if(monstersFile.exists()) return;
		
		String url = "https://www.osrsbox.com/osrsbox-db/monsters-complete.json";
		FileWriter fw = new FileWriter(monstersFile);
		
		InputStream is = new URL(url).openStream();
		 try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		     
		      
		      StringBuilder sb = new StringBuilder();
				int cp;
				while ((cp = rd.read()) != -1) {
					sb.append((char) cp);
				}

				String jsonText = sb.toString();
				fw.write(jsonText);
		    } finally {
		      is.close();
		      fw.close();
		    }
	}
	
	public static String OSBotDataDir() {
		return System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "Data";
	}
	
}
