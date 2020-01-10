package com.skeeter144.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
	
	List<String> monsterNames = new ArrayList<String>();
	String searchText = "";
	
	public SBKillerGui(SBKiller script) {
		this.setSize(518, 351);
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
		
		JLabel lblspaceSeparatedList = new JLabel("(Space Separated List)");
		lblspaceSeparatedList.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblspaceSeparatedList.setBounds(286, 48, 149, 16);
		getContentPane().add(lblspaceSeparatedList);
		
		ironManCb = new JCheckBox("Iron Man Mode");
		ironManCb.setBounds(12, 95, 141, 25);
		getContentPane().add(ironManCb);
		
		startBtn = new JButton("Start");
		startBtn.setBounds(171, 231, 149, 47);
		getContentPane().add(startBtn);
		
		monsterListCb = new JComboBox<String>();
		monsterListCb.setBounds(286, 17, 162, 20);
		getContentPane().add(monsterListCb);
		monsterListCb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				targetTf.setText((String)monsterListCb.getSelectedItem());
			}
		});
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startBot();
			}
		});
		
		updateMonsterCb();
	}
	
	void updateMonsterCb() {
		script.getMethodProvider().npcs.getAll().forEach(
				item -> {
					if(item.isAttackable() && !monsterNames.contains(item.getName())) { 
						monsterNames.add(item.getName());
					}
				});

		monsterListCb.removeAllItems();
		monsterNames.forEach(item -> monsterListCb.addItem(item));
	}
	
	void startBot() {
		script.targetName = targetTf.getText();
		script.setTargetItems(targetItemsTf.getText());
		script.buryBones = buryBonesCb.isSelected();
		script.running = true;
		
		this.setVisible(false);
	}
}
