package com.skeeter144.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.skeeter144.main.MainScript;
import com.skeeter144.script.SkeeterScript;
import javax.swing.SwingConstants;

public class MainScreen extends JFrame{
	
	JComboBox<SkeeterScript> scriptsCb;
	JButton startBtn;
	JButton stopBtn;
	JButton pauseBtn;
	JLabel statusLbl;
	
	MainScript mainScript;
	String status = "";
	
	public MainScreen(MainScript mainScript) {
		this.mainScript = mainScript;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("SkeeterBot3000");
		getContentPane().setLayout(null);
		setSize(406, 240);
		setResizable(false);
		SkeeterScript[] scripts = new SkeeterScript[mainScript.scripts.size()];
		
		scriptsCb = new JComboBox<SkeeterScript>(mainScript.scripts.toArray(scripts));
		scriptsCb.setBounds(136, 12, 254, 20);
		getContentPane().add(scriptsCb);
		
		startBtn = new JButton("Start");
		startBtn.setBounds(10, 11, 116, 23);
		getContentPane().add(startBtn);
		startBtn.addActionListener((e) -> { mainScript.startScript(); }); 
		
		pauseBtn = new JButton("Pause");
		pauseBtn.setBounds(10, 11, 116, 23);
		getContentPane().add(pauseBtn);
		pauseBtn.addActionListener((e) -> { mainScript.pauseScript(); }); 
		
		stopBtn = new JButton("Stop");
		stopBtn.setEnabled(false);
		stopBtn.setBounds(10, 39, 116, 23);
		getContentPane().add(stopBtn);
		stopBtn.addActionListener((e) -> { mainScript.stopScript();} ); 
		
		JLabel statusTitle = new JLabel("Status: ");
		statusTitle.setBounds(10, 186, 45, 14);
		getContentPane().add(statusTitle);
		
		JLabel percentLbl = new JLabel("100%");
		percentLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		percentLbl.setBounds(350, 186, 40, 14);
		getContentPane().add(percentLbl);
		
		statusLbl = new JLabel("Idle");
		statusLbl.setBounds(53, 186, 231, 14);
		getContentPane().add(statusLbl);
	}
	
	

	private static final long serialVersionUID = 4721909832061841141L;
}
