package com.skeeter144.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.skeeter144.main.MainScript;
import com.skeeter144.script.SBKiller;

public class SBKillerGui extends JFrame{
	
	private static final long serialVersionUID = -1575479407796704136L;
	JTextField targetTf;
	JTextField targetItemsTf;
	JCheckBox ironManCb;
	JButton startBtn;
	JCheckBox buryBonesCb;
	JComboBox<String> monsterListCb;
	
	SBKiller script;
	
	String searchText = "";
	private JButton homeBtn;
	private JButton refreshEntitiesBtn;
	private JButton loadDropsBtn;
	
	public SBKillerGui(SBKiller script) {
		this.setSize(590, 331);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.script = script;
		
		getContentPane().setLayout(null);
		
		targetTf = new JTextField();
		targetTf.setBounds(160, 16, 116, 22);
		getContentPane().add(targetTf);
		targetTf.setColumns(10);
		targetTf.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				searchText = targetTf.getText();
				script.getMethodProvider().logger.debug(searchText);
			}
		});
		
		JLabel targetLbl = new JLabel("Target:");
		targetLbl.setBounds(12, 16, 56, 16);
		getContentPane().add(targetLbl);
		
		targetItemsTf = new JTextField();
		targetItemsTf.setBounds(160, 45, 116, 22);
		getContentPane().add(targetItemsTf);
		targetItemsTf.setColumns(10);
		
		JLabel targetItemsLbl = new JLabel("Target Items:");
		targetItemsLbl.setBounds(12, 45, 95, 16);
		getContentPane().add(targetItemsLbl);
		
		buryBonesCb = new JCheckBox("Bury Bones");
		buryBonesCb.setBounds(12, 70, 113, 25);
		getContentPane().add(buryBonesCb);
		
		ironManCb = new JCheckBox("Iron Man Mode");
		ironManCb.setBounds(12, 95, 141, 25);
		getContentPane().add(ironManCb);
		
		startBtn = new JButton("Start");
		startBtn.setBounds(415, 234, 150, 50);
		getContentPane().add(startBtn);
		
		monsterListCb = new JComboBox<String>();
		monsterListCb.setBounds(288, 17, 184, 20);
		getContentPane().add(monsterListCb);
		
//		ImageIcon icon = new ImageIcon("/res/home.png");
//		Image img = icon.getImage();  
//		Image newimg = img.getScaledInstance( 64, 64,  java.awt.Image.SCALE_SMOOTH ) ;  
//		icon = new ImageIcon(newimg);
		
		homeBtn = new JButton("Home");
		homeBtn.addActionListener((e) -> {
			setVisible(false);
			MainScript.instance().showMainMenu();
		});
//		homeBtn.setIcon(icon);
		homeBtn.setBounds(12, 259, 82, 25);
		getContentPane().add(homeBtn);
		
		
//		icon = new ImageIcon("/res/refresh.png");
//		img = icon.getImage();
//		newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH );  
//		icon = new ImageIcon(newimg);
		
		refreshEntitiesBtn = new JButton("Refresh");
		refreshEntitiesBtn.addActionListener((e) -> {
			updateMonsterCb();
		});
		
		refreshEntitiesBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
//		refreshEntitiesBtn.setIcon(icon);
		refreshEntitiesBtn.setBounds(482, 16, 82, 22);
		getContentPane().add(refreshEntitiesBtn);
		
		loadDropsBtn = new JButton("Load Drops");
		loadDropsBtn.setBounds(286, 45, 128, 23);
		getContentPane().add(loadDropsBtn);
		monsterListCb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				targetTf.setText((String)monsterListCb.getSelectedItem());
			}
		});
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(script.running) {
					pauseBot();
					startBtn.setText("Resume");
				}else {
					startBot();
					startBtn.setText("Pause");
				}
			}
		});
		
		updateMonsterCb();
	}
	
	void updateMonsterCb() {
		HashSet<String> monsterNames = new HashSet<String>();
		script.getMethodProvider().npcs.getAll().forEach(
				item -> {
					if(item.getName() != null && !item.getName().equalsIgnoreCase("null") 
							&& item.hasAction("Attack")) { 
						monsterNames.add(item.getName());
					}
				});

		monsterListCb.removeAllItems();
		monsterNames.forEach(item -> monsterListCb.addItem(item));
		monsterNames.forEach(item -> script.getMethodProvider().logger.debug(item));
	}
	
	void startBot() {
		script.targetName = targetTf.getText();
		script.setTargetItems(targetItemsTf.getText());
		script.buryBones = buryBonesCb.isSelected();
		script.running = true;
		
		this.setVisible(false);
	}
	
	void pauseBot() {
		script.running = false;
	}
}
