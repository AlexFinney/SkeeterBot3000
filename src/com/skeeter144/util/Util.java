package com.skeeter144.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import org.osbot.utility.Logger;

import com.skeeter144.main.MainScript;

public class Util {

	public static void copyResourcesToDataDir() {
//		File resourcesDir = new File(OSBotDataDir() + "/SB300/res");
//			if (!resourcesDir.isDirectory()) {
//				copyDirectory(new File("/res"), resourcesDir);
//			}

	}

	public static String OSBotDataDir() {
		return System.getProperty("user.home") + File.separator + "OSBot" + File.separator + "Data";
	}

	public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir);
		} else {
			Files.copy(sourceDir.toPath(), targetDir.toPath());
		}
	}

	private static void copyDirectoryRecursively(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String child : source.list()) {
			copyDirectory(new File(source, child), new File(target, child));
		}
	}
	
	public static File getFileFromURL(ClassLoader cl, String fileName) {
	    URL url = cl.getResource(fileName);
	    File file = null;
	    try {
	        file = new File(url.toURI());
	    } catch (URISyntaxException e) {
	        file = new File(url.getPath());
	    }
	    return file;
	}
	
	static ArrayList<String> queuedMsgs = new ArrayList<>();
	public static void log(String msg) {
		Logger logger = MainScript.instance().logger;
		if(logger != null) {
			while(!queuedMsgs.isEmpty()) {
				logger.debug(queuedMsgs.remove(queuedMsgs.size() - 1));
			}
			logger.debug(msg);
		}else {
			queuedMsgs.add(0, msg);
		}
	}
	
	/**
	 * 
	 * @param script	Most likely: client.getBot().getScriptExecutor().getCurrent()
	 * @param g			The Graphics2D object passed into the onPaint() method
	 * @param position	The Position for the tile you are wishing to draw on
	 * @param tileColor	The color that will be drawn around the border of the tile
	 * @param textColor	The color of the text for the "s" parameter
	 * @param s			The text you wish to display next to the tile, if any.
	 */
	public static void drawTile(Script script, Graphics g, Position position, Color tileColor, Color textColor, String s) {
		Polygon polygon;
		    if (position != null && (polygon = position.getPolygon(script.getBot(), position.getTileHeight(script.getBot()))) != null) {
		        g.setColor(tileColor);
		        for (int i = 0; i < polygon.npoints; i++) {
		            g.setColor(new Color(0, 0, 0, 20));
		            g.fillPolygon(polygon);
		            g.setColor(tileColor);
		            g.drawPolygon(polygon);
		        }
		        
		        if(!s.isEmpty()) {
			        g.setColor(textColor);
			        g.drawString(s, (int) polygon.getBounds().getX(), (int) polygon.getBounds().getY());
		        }
		    }
		}
	
	
	/**
	 * 
	 * @param script	Most likely: client.getBot().getScriptExecutor().getCurrent()
	 * @param g			The Graphics2D object passed into the onPaint() method
	 * @param position	The Position for the tile you are wishing to draw on
	 * @param tileColor	The color that will be drawn around the border of the tile
	 */
	public static void drawTile(Script script, Graphics g, Position position, Color tileColor) {
		drawTile(script, g, position, tileColor, Color.WHITE, "");
	}
	
	public static int walkingDistToPosition(Script script, Position to) {
		return walkingDistToPosition(script, script.myPosition(), to);
	}
	
	public static int walkingDistToPosition(Script script, Position from, Position to) {
		int bestDist = Integer.MAX_VALUE;
		
		int dist = script.getMap().realDistance(from, to);
		if(dist >= 0 && dist < bestDist) bestDist = dist;
		
		dist = script.getMap().realDistance(from, to.translate(0, 1));
		if(dist >= 0 && dist < bestDist) bestDist = dist;
		
		dist = script.getMap().realDistance(from, to.translate(0, -1));
		if(dist >= 0 && dist < bestDist) bestDist = dist;
		
		dist = script.getMap().realDistance(from, to.translate(1, 0));
		if(dist >= 0 && dist < bestDist) bestDist = dist;
		
		dist = script.getMap().realDistance(from, to.translate(-1, 0));
		if(dist >= 0 && dist < bestDist) bestDist = dist;
		
		return bestDist;
	}
}
