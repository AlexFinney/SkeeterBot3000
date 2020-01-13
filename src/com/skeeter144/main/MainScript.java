package com.skeeter144.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.keyboard.BotKeyListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import com.skeeter144.data.MonsterData;
import com.skeeter144.gui.MainScreen;
import com.skeeter144.misc.Formatting;
import com.skeeter144.script.SBCooker_AlKharid;
import com.skeeter144.script.SBFisher;
import com.skeeter144.script.SBKiller;
import com.skeeter144.script.SBPizzaBaser;
import com.skeeter144.script.SkeeterScript;
import com.skeeter144.script.SkeeterScript.State;
import com.skeeter144.util.Util;

@ScriptManifest(name = "SkeeterBot3000", author = "Skeeter144", version = 1.0, info = "The bestest bot there ever was. Skynet's great-grandaddy.", logo = "")
public class MainScript extends Script{
	
	public boolean filesDownloaded = false;
	
	long startTime = 0;
	public ArrayList<SkeeterScript> scripts = new ArrayList<SkeeterScript>();
	
	SkeeterScript activeScript;
	
	public boolean running = false;
    public boolean ironManMode = false;
	public String status;

	long lastXpUpdate = -1;
	boolean showStats = true;
	
	Map<Skill, Integer> xpEarned = new HashMap<Skill, Integer>();
	
	MainScreen mainMenu;
	
	List<Point> mouseTrail = new LinkedList<>();
	
	public MainScript() {
		super();
		instance = this;
		Thread downloadThread = new Thread(() -> {
			MonsterData.loadMonsterDrops(null);
		});
		downloadThread.start();
		
		Thread fileMoverThread = new Thread(() -> {
			Util.copyResourcesToDataDir();
		});
		fileMoverThread.start();
		
		Thread mouseTrailThread = new Thread(() -> {
			long lastRemoveTime = 0;
			while(true) {
				try {
					if(mouse == null) {
						Thread.sleep(200);
						continue;
					}
					
					mouseTrail.add(mouse.getPosition());

					if(mouseTrail.size() > 500) {
						synchronized(mouseTrail) {
							mouseTrail.remove(0);
						}
					}
					
					if(mouseTrail.size() > 0 && System.currentTimeMillis() - 20 > lastRemoveTime) {
						synchronized(mouseTrail) {
							mouseTrail.remove(0);
						}
						lastRemoveTime = System.currentTimeMillis();
					}
					
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		mouseTrailThread.start();
	}
	
	
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
		
		experienceTracker.startAll();
		
		getBot().addMessageListener(this);
		getBot().addKeyListener(new BotKeyListener() {
			@Override
			public void checkKeyEvent(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_CONTROL && e.getID() == KeyEvent.KEY_RELEASED) {
					if(activeScript == null) mainMenu.setVisible(!mainMenu.isVisible());
					else if(activeScript.gui != null) activeScript.gui.setVisible(!activeScript.gui.isVisible());
				}
			}
		});
		
		scripts.add(new SBKiller(this));
		scripts.add(new SBFisher(this));
		scripts.add(new SBPizzaBaser(this));
		scripts.add(new SBCooker_AlKharid(this));
		
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
		
		synchronized(mouseTrail) {
			g.setColor(Color.RED);
			for(int i = 0; i < mouseTrail.size() - 1; ++i) {
				g.setColor(new Color(1, 0, 0, (((float)i) / mouseTrail.size())));
				g.drawLine((int)mouseTrail.get(i).getX(), (int)mouseTrail.get(i).getY(), 
						(int)mouseTrail.get(i+1).getX(), (int)mouseTrail.get(i+1).getY());
			}
		}
		
		
		if(showStats) {
			g.setColor(Color.WHITE);
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
	
	public void showMainMenu() {
		mainMenu.setVisible(true);
	}
	
	static MainScript instance;
	public static MainScript instance() {
		return instance;
	}
}
