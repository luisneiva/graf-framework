package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GRAFSplash extends JFrame{
	private static GRAFSplash INSTANCE;
	private ImageIcon logoIcon;
	private JProgressBar progress;
	private JPanel logoPanel;
	private JLabel activityLabel;
	
	
	
	public static GRAFSplash getInstance(){
		if(INSTANCE == null)
			GRAFSplash.INSTANCE = new GRAFSplash();
		return INSTANCE;
	}
	
	public static void destroyInstance(){
		if(INSTANCE != null)
			INSTANCE.setVisible(false);
		INSTANCE = null;
	}
	
	private GRAFSplash(){
		super("Loading GRAF....");
		
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setSize(400,20);
		
		this.setLayout(new GridLayout(1,1));
//		this.setUndecorated(true);
		this.setLocation(dim.width / 2 - 150, dim.height / 2 - 50);
		
		logoPanel = new JPanel();
		logoPanel.setSize(300, 80);
		progress = new JProgressBar(0,100);
		progress.setSize(300, 20);
		logoIcon = new ImageIcon(controller.Properties.getProperty("LogoPath"));
		activityLabel = new JLabel();
		activityLabel.setHorizontalAlignment(JLabel.CENTER);
		activityLabel.setVerticalAlignment(JLabel.CENTER);
		
		progress.setLayout(new BorderLayout());
		progress.add(activityLabel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		
		progress.setIndeterminate(true);
		
//		this.add(logoPanel, BorderLayout.CENTER);
		this.add(progress);
	}
	
	public void setVisible(Boolean b){
		super.setVisible(b);
		if(b == true){
			Graphics2D gg = (Graphics2D) logoPanel.getGraphics();
			gg.drawString("GRAF", 0, 0);
			logoPanel.repaint();
		}
	}
	
	public static void setPercent(Integer i){
		INSTANCE.progress.setValue(i);
	}
	
	public static void setActivityString(String ac){
		INSTANCE.activityLabel.setText(ac);
	}
	
	public void repaint(){
		super.repaint();
		Graphics2D gg = (Graphics2D) logoPanel.getGraphics();
		//gg.drawImage(logoIcon.getImage(), 0, 0, null);
		gg.setFont(Font.getFont("Arial"));
		gg.drawString("GRAF", 0, 0);
		logoPanel.repaint();
	}
}
