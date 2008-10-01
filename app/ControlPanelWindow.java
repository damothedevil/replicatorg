/*
  Part of the ReplicatorG project - http://www.replicat.org
  Copyright (c) 2008 Zach Smith

  Forked from Arduino: http://www.arduino.cc

  Based on Processing http://www.processing.org
  Copyright (c) 2004-05 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  
  $Id: Editor.java 370 2008-01-19 16:37:19Z mellis $
*/

package processing.app;

import processing.app.drivers.*;
import processing.app.models.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.vecmath.*;
import java.util.*;
import java.util.regex.*;

public class ControlPanelWindow extends JFrame implements ActionListener, ChangeListener
{
	protected JPanel mainPanel;
	protected JPanel jogPanel;
	protected JButton xPlusButton;
	protected JButton xMinusButton;
	protected JButton yPlusButton;
	protected JButton yMinusButton;
	protected JButton zPlusButton;
	protected JButton zMinusButton;
	protected JButton zeroButton;

	protected JPanel extruderPanel;
	
	protected double jogRate;
	protected Pattern jogPattern;
	protected String[] jogStrings = {"0.01mm", "0.05mm", "0.1mm", "0.5mm", "1mm", "5mm", "10mm", "20mm", "50mm"};
	
	protected JSlider xyFeedrateSlider;
	protected JSlider zFeedrateSlider;
	
	protected JTextField xPosField;
	protected JTextField yPosField;
	protected JTextField zPosField;
	
	JTabbedPane toolsPane;
	
	protected MachineController machine;
	protected Driver driver;
	
	public ControlPanelWindow (MachineController m)
	{
		super("Control Panel");
		
		//save our machine!
		machine = m;
		driver = machine.getDriver();
		
		//make it a reasonable size
 		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		//int myWidth = screen.width-40;
		//int myHeight = screen.height-40;
		int myWidth = 450;
		int myHeight = 700;
		
		//compile our regexes
		jogRate = 10.0;
		jogPattern = Pattern.compile("([.0-9]+)");
		//jogStrings = ;
		
	 	setBounds(40, 40, myWidth, myHeight);
	
		//default behavior
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	
		//no resizing... yet
		setResizable(false);
		
		//no menu bar.
		setMenuBar(null);
		
		//create all our GUI interfaces
		mainPanel = new JPanel();
		BoxLayout bl = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS);
		mainPanel.setLayout(bl);
		createJogPanel();
		createToolsPanel();
		add(mainPanel);

		//where are we?
		updatePosition();
	}
	
	protected void createJogPanel()
	{
		//how big you want 'em boss?
		int buttonSize = 60;
		int textBoxWidth = 160;
		
		//create our X+ button
		xPlusButton = new JButton("X+");
		//xPlusButton.setMnemonic(KeyEvent.VK_KP_RIGHT);
		xPlusButton.setToolTipText("Jog X axis in positive direction");
		xPlusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		xPlusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		xPlusButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
		xPlusButton.addActionListener(this);
		
		//create our X- button
		xMinusButton = new JButton("X-");
		//xMinusButton.setMnemonic(KeyEvent.VK_KP_LEFT);
		xMinusButton.setToolTipText("Jog X axis in negative direction");
		xMinusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		xMinusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		xMinusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		xMinusButton.addActionListener(this);

		//create our Y+ button
		yPlusButton = new JButton("Y+");
		//yPlusButton.setMnemonic(KeyEvent.VK_KP_UP);
		yPlusButton.setToolTipText("Jog Y axis in positive direction");
		yPlusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		yPlusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		yPlusButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
		yPlusButton.addActionListener(this);

		//create our Zero button
		zeroButton = new JButton("Zero");
		//zero.setMnemonic(KeyEvent.VK_ZERO);
		zeroButton.setToolTipText("Mark Current Position as Zero (0,0,0)");
		zeroButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		zeroButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		zeroButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
		zeroButton.addActionListener(this);

		//create our Y- button
		yMinusButton = new JButton("Y-");
		//yMinusButton.setMnemonic(KeyEvent.VK_KP_DOWN);
		yMinusButton.setToolTipText("Jog Y axis in negative direction");
		yMinusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		yMinusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		yMinusButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
		yMinusButton.addActionListener(this);

		//create our Z+ button
		zPlusButton = new JButton("Z+");
		//zPlusButton.setMnemonic(KeyEvent.VK_PLUS);
		zPlusButton.setToolTipText("Jog Z axis in positive direction");
		zPlusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		zPlusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		zPlusButton.setMinimumSize(new Dimension(buttonSize, buttonSize));
		zPlusButton.addActionListener(this);
		
		//create our Z- button
		zMinusButton = new JButton("Z-");
		//zMinusButton.setMnemonic(KeyEvent.VK_MINUS);
		zMinusButton.setToolTipText("Jog Z axis in negative direction");
		zMinusButton.setMaximumSize(new Dimension(buttonSize, buttonSize));
		zMinusButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
		zMinusButton.addActionListener(this);

		//create our position panel
		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.PAGE_AXIS));
		//positionPanel.setLayout(new GroupLayout(positionPanel));
		
		//our label
		JLabel jogLabel = new JLabel("Jog Size");
		jogLabel.setHorizontalAlignment(JLabel.LEFT);

		//create our jog size dropdown
		JComboBox jogList = new JComboBox(jogStrings);
		//TODO: pull this from prefs
		jogList.setSelectedIndex(6);
		jogList.setMaximumSize(new Dimension(textBoxWidth, 25));
		jogList.setMinimumSize(new Dimension(textBoxWidth, 25));
		jogList.setPreferredSize(new Dimension(textBoxWidth, 25));
		jogList.setActionCommand("jog size");
		jogList.addActionListener(this);

		//our labels
		JLabel xPosLabel = new JLabel("X Position");
		xPosLabel.setHorizontalAlignment(JLabel.LEFT);
		JLabel yPosLabel = new JLabel("Y Position");
		yPosLabel.setHorizontalAlignment(JLabel.LEFT);
		JLabel zPosLabel = new JLabel("Z Position");
		zPosLabel.setHorizontalAlignment(JLabel.LEFT);

		//our position text boxes
		xPosField = new JTextField();
		xPosField.setMaximumSize(new Dimension(textBoxWidth, 25));
		xPosField.setMinimumSize(new Dimension(textBoxWidth, 25));
		xPosField.setPreferredSize(new Dimension(textBoxWidth, 25));

		yPosField = new JTextField();
		yPosField.setMaximumSize(new Dimension(textBoxWidth, 25));
		yPosField.setMinimumSize(new Dimension(textBoxWidth, 25));
		yPosField.setPreferredSize(new Dimension(textBoxWidth, 25));

		zPosField = new JTextField();
		zPosField.setMaximumSize(new Dimension(textBoxWidth, 25));
		zPosField.setMinimumSize(new Dimension(textBoxWidth, 25));
		zPosField.setPreferredSize(new Dimension(textBoxWidth, 25));

		//our 'go' button
		JButton goButton = new JButton("Go.");

		goButton.setToolTipText("Go to selected coordinates.");
		//goButton.setMaximumSize(new Dimension(textBoxWidth, 25));
		//goButton.setMinimumSize(new Dimension(textBoxWidth, 25));
		//goButton.setPreferredSize(new Dimension(textBoxWidth, 25));
		goButton.addActionListener(this);
		goButton.setEnabled(false);

		//add them all to position panel		
		positionPanel.add(jogLabel);
		positionPanel.add(jogList);
		positionPanel.add(xPosLabel);
		positionPanel.add(xPosField);
		positionPanel.add(yPosLabel);
		positionPanel.add(yPosField);
		positionPanel.add(zPosLabel);
		positionPanel.add(zPosField);
		positionPanel.add(goButton);
		
		//create our XY panel
		JPanel xyPanel = new JPanel();
		xyPanel.setLayout(new BoxLayout(xyPanel, BoxLayout.LINE_AXIS));
		xyPanel.add(xMinusButton);
		
		//another panel to hold the vertical stuff
		JPanel yPanel = new JPanel();
		yPanel.setLayout(new BoxLayout(yPanel, BoxLayout.PAGE_AXIS));
		yPanel.add(yPlusButton);
		yPanel.add(zeroButton);
		yPanel.add(yMinusButton);
		xyPanel.add(yPanel);
		
		//finally our last button.
		xyPanel.add(xPlusButton);
		
		//our z panel too
		JPanel zPanel = new JPanel();
		zPanel.setLayout(new BoxLayout(zPanel, BoxLayout.PAGE_AXIS));
		zPanel.add(zPlusButton);
		//zPanel.add(Box.createVerticalGlue());
		zPanel.add(zMinusButton);

		//add them both to our xyz panel
		JPanel xyzPanel = new JPanel();
		xyzPanel.setLayout(new BoxLayout(xyzPanel, BoxLayout.LINE_AXIS));
		xyzPanel.add(xyPanel);
		xyzPanel.add(Box.createHorizontalGlue());
		xyzPanel.add(zPanel);
		xyzPanel.add(Box.createHorizontalGlue());
		xyzPanel.add(positionPanel);
				
		//create our xy slider
		//TODO: pull these values from our machine config!
		xyFeedrateSlider = new JSlider(JSlider.HORIZONTAL, 1, 5000, 1000);
		xyFeedrateSlider.setMajorTickSpacing(1000);
		xyFeedrateSlider.setMinorTickSpacing(100);
		xyFeedrateSlider.setName("xy-feedrate-slider");
		//xyFeedrateSlider.addChangeListener(this);
		
		//our label
		JLabel xyFeedrateLabel = new JLabel("XY Feedrate (mm/min.)");
		xyFeedrateLabel.setVerticalAlignment(JLabel.BOTTOM);
		
		//create the xyfeedrate panel
		JPanel xyFeedratePanel = new JPanel();
		xyFeedratePanel.setLayout(new BoxLayout(xyFeedratePanel, BoxLayout.LINE_AXIS));
		
		//add our components
		xyFeedratePanel.add(xyFeedrateLabel);
		xyFeedratePanel.add(xyFeedrateSlider);

		//create our z slider
		zFeedrateSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 50);
		zFeedrateSlider.setMajorTickSpacing(10);
		zFeedrateSlider.setMinorTickSpacing(1);
		//zFeedrateSlider.addChangeListener(this);
		zFeedrateSlider.setName("z-feedrate-slider");

		//our label
		JLabel zFeedrateLabel = new JLabel("Z Feedrate (mm/min.)");
		zFeedrateLabel.setVerticalAlignment(JLabel.BOTTOM);
		
		//create the xyfeedrate panel
		JPanel zFeedratePanel = new JPanel();
		zFeedratePanel.setLayout(new BoxLayout(zFeedratePanel, BoxLayout.LINE_AXIS));
		
		//add our components
		zFeedratePanel.add(zFeedrateLabel);
		zFeedratePanel.add(zFeedrateSlider);

		//create our jog panel
		jogPanel = new JPanel();
		jogPanel.setLayout(new BoxLayout(jogPanel, BoxLayout.PAGE_AXIS));
		
		//proper size!
		//jogPanel.setMinimumSize(new Dimension(420, 300));
		//jogPanel.setMaximumSize(new Dimension(420, 300));
		//jogPanel.setPreferredSize(new Dimension(420, 300));

		//add it all to our jog panel
		jogPanel.add(xyzPanel);
		jogPanel.add(xyFeedratePanel);
		jogPanel.add(zFeedratePanel);
		
		//add jog panel border and stuff.
		jogPanel.setBorder(BorderFactory.createTitledBorder("Jog Controls"));		
		
		//add the whole deal to our window.
		mainPanel.add(jogPanel);
	}
	
	protected void createToolsPanel()
	{
		toolsPane = new JTabbedPane();
		
		for (Enumeration e = machine.getModel().getTools().elements(); e.hasMoreElements();)
		{
			ToolModel t = (ToolModel)e.nextElement();
			
			if (t.getType().equals("extruder"))
			{
				System.out.println("Creating panel for " + t.getName());
				createExtruderPanel(t);
			}
			else
			{
				System.out.println("Unsupported tool for control panel.");
			}
		}
		
		//add it all in.
		//JPanel toolsPanel = new JPanel();
		//toolsPanel.add(toolsPane);
		mainPanel.add(toolsPane);
	}
	
	protected void createExtruderPanel(ToolModel t)
	{
		int textBoxWidth = 150;
		Dimension labelMinimumSize = new Dimension(100, 25);
		
		//create our initial panel
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		panel.setLayout(layout);
		//GridLayout extruderGrid = new GridLayout(0, 2);
		//panel.setLayout(extruderGrid);
		
		//create our motor options
		if (t.hasMotor())
		{
			//our motor speed vars
			JLabel motorSpeedLabel = new JLabel("Motor Speed");
			motorSpeedLabel.setMinimumSize(labelMinimumSize);
			motorSpeedLabel.setMaximumSize(labelMinimumSize);
			motorSpeedLabel.setPreferredSize(labelMinimumSize);
			motorSpeedLabel.setHorizontalAlignment(JLabel.LEFT);
			
			JTextField motorSpeedField = new JTextField();
			motorSpeedField.setMaximumSize(new Dimension(textBoxWidth, 25));
			motorSpeedField.setMinimumSize(new Dimension(textBoxWidth, 25));
			motorSpeedField.setPreferredSize(new Dimension(textBoxWidth, 25));
			
			//create our motor options
			JLabel motorEnabledLabel = new JLabel("Motor");
			motorEnabledLabel.setMinimumSize(labelMinimumSize);
			motorEnabledLabel.setHorizontalAlignment(SwingConstants.LEFT);

			JCheckBox motorEnabledCheck = new JCheckBox("enable");

			//add it to the panel(s)
			JPanel motorSpeedPanel = new JPanel();
			motorSpeedPanel.setMinimumSize(new Dimension(420, 30));
			motorSpeedPanel.setPreferredSize(new Dimension(420, 30));
			motorSpeedPanel.setMaximumSize(new Dimension(420, 30));
			motorSpeedPanel.setAlignmentY((float)0.0);
			motorSpeedPanel.add(motorSpeedLabel);
			motorSpeedPanel.add(motorSpeedField);
			panel.add(motorSpeedPanel);

			JPanel motorControlPanel = new JPanel();
			motorControlPanel.setMaximumSize(new Dimension(420, 30));
			motorControlPanel.add(motorEnabledLabel);
			motorControlPanel.add(motorEnabledCheck);
			panel.add(motorControlPanel);
		}
		
		//our temperature fields
		if (t.hasHeater())
		{
			JLabel targetTempLabel = new JLabel("Target Temperature");
			JTextField targetTempField = new JTextField();
			targetTempField.setMaximumSize(new Dimension(textBoxWidth, 25));
			targetTempField.setMinimumSize(new Dimension(textBoxWidth, 25));
			targetTempField.setPreferredSize(new Dimension(textBoxWidth, 25));

			JLabel currentTempLabel = new JLabel("Current Temperature");
			JTextField currentTempField = new JTextField();
			currentTempField.setMaximumSize(new Dimension(textBoxWidth, 25));
			currentTempField.setMinimumSize(new Dimension(textBoxWidth, 25));
			currentTempField.setPreferredSize(new Dimension(textBoxWidth, 25));

			JPanel targetTempPanel = new JPanel();
			targetTempPanel.setMaximumSize(new Dimension(420, 30));
			targetTempPanel.add(targetTempLabel);
			targetTempPanel.add(targetTempField);
			panel.add(targetTempPanel);
			
			JPanel currentTempPanel = new JPanel();	
			currentTempPanel.setMaximumSize(new Dimension(420, 30));
			currentTempPanel.add(currentTempLabel);
			currentTempPanel.add(currentTempField);
			panel.add(currentTempPanel);
		}
		
		//flood coolant controls
		if (t.hasFloodCoolant())
		{
			JLabel floodCoolantLabel = new JLabel("Flood Coolant");
			JCheckBox floodCoolantCheck = new JCheckBox("enable");
			
			JPanel floodCoolantPanel = new JPanel();	
			floodCoolantPanel.setMaximumSize(new Dimension(420, 30));
			floodCoolantPanel.add(floodCoolantLabel);
			floodCoolantPanel.add(floodCoolantCheck);
			panel.add(floodCoolantPanel);
		}
		
		//mist coolant controls
		if (t.hasMistCoolant())
		{
			JLabel mistCoolantLabel = new JLabel("Mist Coolant");
			JCheckBox mistCoolantCheck = new JCheckBox("enable");
			
			JPanel mistCoolantPanel = new JPanel();
			mistCoolantPanel.setMaximumSize(new Dimension(420, 30));
			mistCoolantPanel.add(mistCoolantLabel);
			mistCoolantPanel.add(mistCoolantCheck);
			panel.add(mistCoolantPanel);
		}
		
		//cooling fan controls
		if (t.hasFan())
		{
			JLabel fanLabel = new JLabel("Cooling Fan");
			JCheckBox fanCheck = new JCheckBox("enable");
			
			JPanel fanPanel = new JPanel();	
			fanPanel.setMaximumSize(new Dimension(420, 30));
			fanPanel.add(fanLabel);
			fanPanel.add(fanCheck);
			panel.add(fanPanel);
		}
		
		//valve controls
		if (t.hasValve())
		{
			JLabel valveLabel = new JLabel("Valve");
			JCheckBox valveCheck = new JCheckBox("open");
			
			JPanel valvePanel = new JPanel();	
			valvePanel.setMaximumSize(new Dimension(420, 30));
			valvePanel.add(valveLabel);
			valvePanel.add(valveCheck);
			panel.add(valvePanel);
		}

		//valve controls
		if (t.hasCollet())
		{
			JLabel colletLabel = new JLabel("Collet");
			JCheckBox colletCheck = new JCheckBox("open");
			
			JPanel colletPanel = new JPanel();	
			colletPanel.setMaximumSize(new Dimension(420, 30));
			colletPanel.add(colletLabel);
			colletPanel.add(colletCheck);
			panel.add(colletPanel);
		}

		//add it to our tab panel
		toolsPane.addTab(t.getName(), panel);
	}
	
	protected void updatePosition()
	{
		Point3d current = driver.getCurrentPosition();
		
		xPosField.setText(Double.toString(current.x));
		yPosField.setText(Double.toString(current.y));
		zPosField.setText(Double.toString(current.z));
	}
	
	protected void createExtruderPanel()
	{
		extruderPanel = new JPanel();
		extruderPanel.setLayout(new BoxLayout(extruderPanel, BoxLayout.PAGE_AXIS));
		
		add(extruderPanel);
	}

	public void actionPerformed(ActionEvent e)
	{
		String s = e.getActionCommand();

		Point3d current = driver.getCurrentPosition();
		double xyFeedrate = xyFeedrateSlider.getValue();
		double zFeedrate = zFeedrateSlider.getValue();

		if (s.equals("X+"))
		{
			current.x += jogRate;

			driver.setFeedrate(xyFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("X-"))
		{
			current.x -= jogRate;

			driver.setFeedrate(xyFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("Y+"))
		{
			current.y += jogRate;

			driver.setFeedrate(xyFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("Y-"))
		{
			current.y -= jogRate;

			driver.setFeedrate(xyFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("Z+"))
		{
			current.z += jogRate;

			driver.setFeedrate(zFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("Z-"))
		{
			current.z -= jogRate;

			driver.setFeedrate(zFeedrate);
			driver.queuePoint(current);
		}
		else if (s.equals("Zero"))
		{
			driver.setCurrentPosition(new Point3d());
		}
		//get our new jog rate
		else if (s.equals("jog size"))
		{
			JComboBox cb = (JComboBox)e.getSource();
			String jogText = (String)cb.getSelectedItem();
			
			//look for a decimal number
			Matcher jogMatcher = jogPattern.matcher(jogText);
			if (jogMatcher.find())
				jogRate = Double.parseDouble(jogMatcher.group(1));

			//TODO: save this back to our preferences file.

			//System.out.println("jog rate: " + jogRate);
		}
		else
			System.out.println("Unknown Action Event: " + s);
			
		updatePosition();
	}

  public void stateChanged(ChangeEvent e)
  {
  	/*
    JSlider source = (JSlider)e.getSource();
    if (!source.getValueIsAdjusting())
    {
      int feedrate = (int)source.getValue();
      
      if (source.getName().equals("xy-feedrate-slider"))
      {
        System.out.println("XY Feedrate: " + feedrate);
      }
      else if (source.getName().equals("z-feedrate-slider"))
      {
        System.out.println("Z Feedrate: " + feedrate);
      }
    }
    */

	updatePosition();
  }
}
    
