package com.skeeter144.main;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import com.skeeter144.gui.MainScreen;
import com.skeeter144.misc.Formatting;
import com.skeeter144.script.SBFisher;
import com.skeeter144.script.SBKiller;
import com.skeeter144.script.SkeeterScript;
import com.skeeter144.script.SkeeterScript.State;
import com.skeeter144.util.Util;

@ScriptManifest(name = "SkeeterBot3000", author = "Skeeter144", version = 1.0, info = "The bestest bot there ever was. Skynet's great-grandaddy.", logo = "")
public class MainScript extends Script{
	
	public boolean filesDownloaded = false;
	public MainScript() {
		super();
		Thread downloadThread = new Thread(() -> {
			Util.loadMonsterDrops(() -> { filesDownloaded = true; });
		});
		downloadThread.start();
	}
	
	long startTime = 0;
	public ArrayList<SkeeterScript> scripts = new ArrayList<SkeeterScript>();
	
	SkeeterScript activeScript;
	SkeeterScript nextScript;
	
	public boolean running = false;
    public boolean ironManMode = false;
	public String status;

	long lastXpUpdate = -1;
	boolean showStats = true;
	
	Map<Skill, Integer> xpEarned = new HashMap<Skill, Integer>();
	
	MainScreen mainMenu;
	
	public void startScript(SkeeterScript script) {
		running = true;
		activeScript = script;
		mainMenu.setVisible(false);
		script.onStart();
	}
	
	public void pauseScript() {
		running = false;
	}
	
	public void stopScript() {
		running = false;
	}
	
	
	@Override
	public void onStart() throws InterruptedException {
		super.onStart();
		
		instance = this;
		
		experienceTracker.startAll();
		
		getBot().addMessageListener(this);
		getBot().addKeyListener(new BotKeyListener() {
			@Override
			public void checkKeyEvent(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) mainMenu.setVisible(!mainMenu.isVisible());
			}
		});
		
		scripts.add(new SBKiller(this));
		scripts.add(new SBFisher(this));
		
		mainMenu = new MainScreen(this);
		mainMenu.setVisible(true);
	}
	
	@Override
	public void onExit() throws InterruptedException {
		super.onExit();
		
		if(mainMenu != null) mainMenu.setVisible(false);
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		if(!running || activeScript == null) return 50;
		if(startTime == 0) startTime = System.currentTimeMillis();
		
		return activeScript.onLoop();
	}
	
	final int BASE_DRAW_HEIGHT = 40;
	final int LINE_HEIGHT = 15;
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
		
		if(!running || activeScript == null) return;
		
		if(System.currentTimeMillis() - lastXpUpdate > 250) updateXpEarned();
		
		if(showStats) {
			g.drawString(runningTimeFormatted(),                           1, BASE_DRAW_HEIGHT + LINE_HEIGHT * 1);
			g.drawString("Previous Action: " + activeScript.getState(),    1, BASE_DRAW_HEIGHT + LINE_HEIGHT * 3);
			g.drawString("Current Action:  " + activeScript.nextAction(),  1, BASE_DRAW_HEIGHT + LINE_HEIGHT * 4);
			
			List<Skill> skillsSorted = new ArrayList<Skill>();
			xpEarned.keySet().forEach(skill -> { 
				if(xpEarned.get(skill) > 0) 
					skillsSorted.add(skill);
			});
			
			Collections.sort(skillsSorted, new Comparator<Skill>() {
				public int compare(Skill o1, Skill o2) {
					return o1.name().compareToIgnoreCase(o2.name());
				}
			});
			
			int line = 6;
			for(Skill skill : skillsSorted) {
				g.drawString(skill.name() + " XP: " + xpEarned.get(skill), 1, BASE_DRAW_HEIGHT +  LINE_HEIGHT * line);
				++line;
			}

			activeScript.onPaint(g);
		}
	}
	
	public State getState() {
		return State.IDLE;
	}
	
	protected void updateXpEarned() {
		Skill[] skills = Skill.values();
		for(Skill s : skills)
			xpEarned.put(s, experienceTracker.getGainedXP(s));
		
		lastXpUpdate = System.currentTimeMillis();
	}
	
	public long runningTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public String runningTimeFormatted() {
		return Formatting.msToReadable(runningTime());
	}
	
	
	static MainScript instance;
	public static MainScript instance() {
		return instance;
	}
	
	
	
}
