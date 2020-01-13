package com.skeeter144.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.skeeter144.data.MonsterData;
import com.skeeter144.main.MainScript;
import com.skeeter144.script.SBKiller;

public class SBKillerGui extends JFrame{
	
	private static final long serialVersionUID = -1575479407796704136L;
	
	JCheckBox ironManCb;
	JCheckBox lootItemsCb;
	JCheckBox buryBonesCb;
	JComboBox<String> monsterListCb;
	JLabel dropTableLbl;
	JLabel lootedItemsLbl;
	
	JList<String> dropTableList;
	JList<String> itemsToLootList;
	
	JButton startBtn;
	JButton homeBtn;
	JButton refreshEntitiesBtn;
	JButton loadDropsBtn;
	JButton addDropBtn;
	JButton removeDropBtn;
	
	SBKiller script;
	Map<String, Integer> monsterIds = new HashMap<>();
	
	public SBKillerGui(SBKiller script) {
		setTitle("Skeeter's Loot N Bones");
		this.setSize(590, 331);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.script = script;
		
		getContentPane().setLayout(null);
		
		JLabel targetLbl = new JLabel("Target:");
		targetLbl.setBounds(12, 19, 56, 16);
		getContentPane().add(targetLbl);
		
		buryBonesCb = new JCheckBox("Bury Bones");
		buryBonesCb.setEnabled(false);
		buryBonesCb.setBounds(12, 80, 101, 25);
		getContentPane().add(buryBonesCb);
		
		ironManCb = new JCheckBox("Iron Man");
		ironManCb.setEnabled(false);
		ironManCb.setBounds(12, 108, 101, 25);
		getContentPane().add(ironManCb);
		
		startBtn = new JButton("Start");
		startBtn.setBounds(449, 239, 116, 45);
		getContentPane().add(startBtn);
		
		monsterListCb = new JComboBox<String>();
		monsterListCb.setBounds(123, 17, 316, 20);
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
		refreshEntitiesBtn.setBounds(449, 16, 116, 22);
		getContentPane().add(refreshEntitiesBtn);
		
//		loadDropsBtn = new JButton("Load Drops");
//		loadDropsBtn.setBounds(449, 39, 116, 23);
//		getContentPane().add(loadDropsBtn);
//		loadDropsBtn.addActionListener((evt) -> {
//			loadDrops();
//		});
		
		addDropBtn = new JButton(">");
		addDropBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> selectedItems = dropTableList.getSelectedValuesList();
				moveItemsBetweenPanels(selectedItems, dropTableList, itemsToLootList);
			}
		});
		addDropBtn.setEnabled(false);
		addDropBtn.setBounds(263, 162, 41, 23);
		getContentPane().add(addDropBtn);
		
		removeDropBtn = new JButton("<");
		removeDropBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> selectedItems = itemsToLootList.getSelectedValuesList();
				moveItemsBetweenPanels(selectedItems, itemsToLootList, dropTableList);
			}
		});
		removeDropBtn.setEnabled(false);
		removeDropBtn.setBounds(263, 194, 41, 23);
		getContentPane().add(removeDropBtn);
		
		lootItemsCb = new JCheckBox("Loot Items");
		lootItemsCb.setBounds(12, 54, 97, 23);
		getContentPane().add(lootItemsCb);
		lootItemsCb.addActionListener((evt) -> {
			updateLootGroup(lootItemsCb.isSelected());
		});
		
		dropTableLbl = new JLabel("Drop Table");
		dropTableLbl.setHorizontalAlignment(SwingConstants.CENTER);
		dropTableLbl.setBounds(154, 69, 72, 14);
		getContentPane().add(dropTableLbl);
		
		lootedItemsLbl = new JLabel("Items to Loot");
		lootedItemsLbl.setHorizontalAlignment(SwingConstants.CENTER);
		lootedItemsLbl.setBounds(342, 69, 72, 14);
		getContentPane().add(lootedItemsLbl);
		
		JScrollPane leftScrollPane = new JScrollPane();
		leftScrollPane.setBounds(123, 86, 130, 200);
		getContentPane().add(leftScrollPane);
		leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		dropTableList = new JList<String>(new DefaultListModel<String>());
		leftScrollPane.setViewportView(dropTableList);
		
		JScrollPane rightScrollPane = new JScrollPane();
		rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		rightScrollPane.setBounds(314, 86, 130, 200);
		getContentPane().add(rightScrollPane);
		
		itemsToLootList = new JList<>(new DefaultListModel<String>());
		rightScrollPane.setViewportView(itemsToLootList);
		monsterListCb.addActionListener(new ActionListener() {
			String lastSelection = "";

			public void actionPerformed(ActionEvent e) {
				if (monsterListCb.getSelectedItem() != null && !monsterListCb.getSelectedItem().equals(lastSelection)) {
					lastSelection = (String)monsterListCb.getSelectedItem();
					loadDrops();
				}
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
	
	void moveItemsBetweenPanels(List<String> items, JList<String> from, JList<String> to) {
		DefaultListModel<String> fromModel = (DefaultListModel<String>) from.getModel();
		DefaultListModel<String> toModel = (DefaultListModel<String>) to.getModel();
		
		for(String s : items) {
			toModel.addElement(s);
			fromModel.removeElement(s);
		}
	}
	
	void loadDrops() {
		DefaultListModel<String> model = (DefaultListModel<String>) dropTableList.getModel();
		model.removeAllElements();
		
		int id = monsterIds.get((String)monsterListCb.getSelectedItem());
		List<String> drops = MonsterData.lookupMonsterDrops(id);
		for(String s : drops) {
			if(!model.contains(s))
				model.addElement(s);
		}
	}
	
	void updateLootGroup(boolean enabled) {
		ironManCb.setEnabled(enabled);
		buryBonesCb.setEnabled(enabled);
		dropTableList.setEnabled(enabled);
		itemsToLootList.setEnabled(enabled);
		lootedItemsLbl.setEnabled(enabled);
		dropTableLbl.setEnabled(enabled);
		addDropBtn.setEnabled(enabled);
		removeDropBtn.setEnabled(enabled);
	}
	
	List<String> getLootedDrops(){
		List<String> list = new ArrayList<>(itemsToLootList.getModel().getSize());
		for (int i = 0; i < itemsToLootList.getModel().getSize(); i++) {
		    list.add(itemsToLootList.getModel().getElementAt(i));
		}
		
		return list;
	}
	
	void updateMonsterCb() {
		HashSet<String> monsterNames = new HashSet<String>();
		script.getMethodProvider().npcs.getAll().forEach(
				item -> {
					if(item.getName() != null && !item.getName().equalsIgnoreCase("null") 
							&& item.hasAction("Attack")) { 
						monsterNames.add(item.getName());
						monsterIds.put(item.getName(), item.getId());
					}
				});

		monsterListCb.removeAllItems();
		monsterNames.forEach(item -> monsterListCb.addItem(item));
		monsterNames.forEach(item -> script.getMethodProvider().logger.debug(item));
	}
	
	void startBot() {
		script.targetName = (String)monsterListCb.getSelectedItem();
		script.buryBones = buryBonesCb.isSelected();
		script.setTargetItems(getLootedDrops());
		script.running = true;
		
		this.setVisible(false);
	}
	
	
	void pauseBot() {
		script.running = false;
	}
}
