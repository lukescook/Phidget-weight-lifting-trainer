/*
 * Copyright 2007 Phidgets Inc.  All rights reserved.
 */

import com.phidgets.*;
import com.phidgets.event.*;

public class ProjectPhidgets
{
	//Messages
	public final static String IDLE = "Sign in to use";
	public final static String WELCOME = "Hello ";
	public final static String ZER0 = " 0";
	public final static String PROGRAM_1 = " 1";
	public final static String PROGRAM_1_SELECT= ">1";
	public final static String PROGRAM_2 = " 2";
	public final static String PROGRAM_2_SELECT= ">2";
	public final static String PROGRAM_3 = " 3";
	public final static String PROGRAM_3_SELECT= ">3";
	public final static String SCREEN_SPACE = "       ";
	public final static String BAD_FORM = "Steady Now!";
	public final static String OKAY_FORM = "Almost There";
	public final static String GOOD_FORM = "Nice Job!";
	public final static String LOWER_WEIGHTS = "Lower weightage";
	public final static String RAISE_WEIGHTS = "Raise weightage";
	public final static String SIGNING_OUT = "Goodbye";
	public final static String PHONE_FOUND = "Connected...";
	public final static String SENDING_DATA = "Uploading...";
	public final static String BEGIN_SESSION = (ZER0 + SCREEN_SPACE + ZER0 + SCREEN_SPACE + ZER0);
	
	
	//Timings
	public final static int DELAY_BETWEEN_USERS = 4000;
	public final static int RUN_TIME= 60000;
	
	//Ports
	public final static int SENSOR_SLIDER_PORT = 7;
	public final static int SENSOR_FORCE_PORT = 4;
	public final static int SENSOR_EXIT_BUTTON_PORT = 0;
	public final static int SENSOR_SELECT_BUTTON_PORT = 2;

	//Other variables
	public static boolean onMenu = true;
	public static boolean allowForceReading = true;
	public static int currentWeight = 0;
	public static int currentSelectedProgram = 0;
	public static int goodForm;
	public static int okayForm;
	public static int badForm;
	public static int repCounter;
	public static double lastMotorPos = 0;
	public static String loggedInUser;
	public static int[][][][] session;
	
	//Interfaces
	public static TextLCDPhidget lcd;
	public static InterfaceKitPhidget ik;
	public static ServoPhidget servo;
	
	public static final void main(String args[]) throws Exception {
		//InterfaceKitPhidget ik;
		//ServoPhidget servo;
		//TextLCDPhidget lcd;
		

		servo = new ServoPhidget();
		servo.openAny();
		//System.out.println("waiting for Servo attachment...");
		servo.waitForAttachment();

		servo.setPosition(0, 0);
		
        lcd = new TextLCDPhidget();
		//Example of enabling logging.
		//Phidget.enableLogging(Phidget.PHIDGET_LOG_VERBOSE, null);

        //Useful for making sure Java runs in the first place
		//System.out.println(Phidget.getLibraryVersion());

		ik = new InterfaceKitPhidget();
		ik.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				//System.out.println("attachment of " + ae);
				
			}
		});
		ik.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				//System.out.println("detachment of " + ae);
			}
		});
		ik.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println(ee);
			}
		});
		ik.addInputChangeListener(new InputChangeListener() {
			public void inputChanged(InputChangeEvent oe) {
				//System.out.println(oe);
			
			}
		});
		ik.addOutputChangeListener(new OutputChangeListener() {
			public void outputChanged(OutputChangeEvent oe) {
				//System.out.println(oe);
			}
		});
		ik.addSensorChangeListener(new SensorChangeListener() {
			public void sensorChanged(SensorChangeEvent se) {
				
				int sensor = se.getIndex();
				int value = se.getValue();
				if (sensor == SENSOR_SLIDER_PORT){
					if(onMenu){
						if (value > 900)
						{
							try {
								lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3_SELECT);
								currentSelectedProgram = 3;
							} catch (PhidgetException e) {
								e.printStackTrace();
							}
							
						}
						else if (value < 100)
						{
							try {
								lcd.setDisplayString(1, PROGRAM_1_SELECT + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3);
								currentSelectedProgram = 1;
							} catch (PhidgetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						else if (value < 550 && value > 450){
							try {
								lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2_SELECT + SCREEN_SPACE + PROGRAM_3);
								currentSelectedProgram = 2;
							} catch (PhidgetException e) {
								e.printStackTrace();
							}
							
						}
					}
					
				}
				if (sensor == SENSOR_FORCE_PORT){
					if(repCounter == 0){
						try {
							ik.setOutputState(7, true);
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
					} else {
						try {
							ik.setOutputState(6, false);
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
					}
					if (value > 700 && allowForceReading == true)
					{
						try {
							ik.setOutputState(5, false);
							allowForceReading = false;
							lcd.setDisplayString(0, BAD_FORM);
							badForm++;
							if(currentSelectedProgram == 1){
								repCounter++;
								if(repCounter % 10 == 0){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=22;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							else if(currentSelectedProgram == 2){
								repCounter++;
								if(repCounter % 20 == 0){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=11;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value > 450 && value < 700 && allowForceReading == true)
					{
						try {
							ik.setOutputState(5, true);
							allowForceReading = false;
							lcd.setDisplayString(0, OKAY_FORM);
							if(currentSelectedProgram == 1){
								repCounter++;
								if(repCounter % 10 == 0){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=22;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							else if(currentSelectedProgram == 2){
								repCounter++;
								if(repCounter % 20 == 0){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=11;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							okayForm++;
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value > 200 && value < 450 && allowForceReading == true)
					{
						try {
							allowForceReading = false;
							lcd.setDisplayString(0, GOOD_FORM);
							if(currentSelectedProgram == 1){
								repCounter++;
								if(repCounter % 10 == 0 && currentSelectedProgram == 1){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=22;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							else if(currentSelectedProgram == 2){
								repCounter++;
								if(repCounter % 20 == 0){
									servo.setPosition(0, 0);
									lastMotorPos = 0;
									lcd.setDisplayString(0, "NEXT SET");
									ik.setOutputState(5, true);
									ik.setOutputState(6, false);
								}else{
									lastMotorPos+=11;
									servo.setPosition(0, lastMotorPos);
									ik.setOutputState(6, true);
								}
							}
							goodForm++;
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value < 100 && allowForceReading == false){
						allowForceReading = true;
						
					}
					
					
				}
				
				if (sensor == SENSOR_EXIT_BUTTON_PORT){
					if (value < 200)
					{
						try {
							if(!onMenu){
								lcd.setDisplayString(0, SIGNING_OUT);
								//Save data from this session
								
								//Reset data
								loggedInUser = null;
								/*for(int i = 0; i < session.length; i++){
									session[i] = 0;
								}*/
								
								Thread.sleep(DELAY_BETWEEN_USERS);
								onMenu = true;
								lcd.setDisplayString(0, IDLE);
								lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3);
							}
						} catch (PhidgetException | InterruptedException e) {
							e.printStackTrace();
						}
						
					}
					
					
				}
				
				if (sensor == SENSOR_SELECT_BUTTON_PORT){
					if (value < 500)
					{
						try {
							if(onMenu){
								onMenu = false;
								lcd.setDisplayString(0, "Let's get started");
								lcd.setDisplayString(1, BEGIN_SESSION);
								goodForm = 0;
								okayForm = 0;
								badForm = 0;
							}
						} catch (PhidgetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					
				}
			}
		});
		
		
		servo.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("attachment of " + ae);
			}
		});
		servo.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
			}
		});
		servo.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("error event for " + ee);
			}
		});
		

		

		ik.openAny();
		lcd.openAny();
		//System.out.println("waiting for InterfaceKit attachment...");
		ik.waitForAttachment();
		
		ik.setSensorChangeTrigger(SENSOR_SLIDER_PORT, 50);
		
		//LCS monitor needs a bit of time for it to properly set up, keep this sleep in
		Thread.sleep(500);
		
		if (lcd.getDeviceID() == TextLCDPhidget.PHIDID_TEXTLCD_ADAPTER) {
            //System.out.println("# Screens: " + lcd.getScreenCount());

            //set screen 0 as active
            lcd.setScreen(0);
            lcd.setScreenSize(lcd.PHIDGET_TEXTLCD_SCREEN_2x16);
            lcd.initialize();
        }

        lcd.setDisplayString(0, IDLE);
        lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3);
        lcd.setContrast(100);
        /*
        for (int i = 0; i < 100; i++) {
            lcd.setContrast(i);
            Thread.sleep(50);
        }*/
       
        
	
		if (ik.getInputCount() > 8)
			System.out.println("input(7,8) = (" +
			  ik.getInputState(7) + "," +
			  ik.getInputState(8) + ")");
		

		/*
		for (int j = 0; j < 1000 ; j++) {
			for (int i = 0; i < ik.getOutputCount(); i++) {
				ik.setOutputState(i, true);
			}
			for (int i = 0; i < ik.getOutputCount(); i++)
				ik.setOutputState(i, false);
		}*/
		

		//System.out.println("Outputting events for 30 seconds.");
		Thread.sleep(RUN_TIME);
		//System.out.print("closing...");
		ik.close();
		ik = null;
		System.out.println(" ok");
		
	}
	/*
	public static void changeWeights(int oldSize, int size){
		int foundWeight = -1;
		for(int i = 0; i < 5; i++){
			if(session[i][0][0][0] == size){
				foundWeight = i;
			}
			
		}
		if (foundWeight != -1)
		{
			
			
		}
		
	}*/
	/*
	public static void resetSession()
	{
		for(int i = 0; i < 5; i++)
		{
			//session[i][0][0][0] = [0][0][0][0];
		}
		
	}*/
	
	public static void updateDisplay()
	{
		String gForm = "";
		String oForm = "";
		String bForm = "";
		if(goodForm < 10){
			gForm += " " + goodForm;
		}
		else{
			gForm += goodForm;
			
		}
		if(okayForm < 10){
			oForm += " " + okayForm;
		}
		else{
			oForm += okayForm;
			
		}
		if(badForm < 10){
			bForm += " " + badForm;
		}
		else{
			bForm += badForm;
			
		}
		try {
			lcd.setDisplayString(1, gForm + SCREEN_SPACE + oForm + SCREEN_SPACE + bForm);
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		
	}
}
