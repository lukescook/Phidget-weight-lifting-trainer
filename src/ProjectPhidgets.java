/*
 * Copyright 2007 Phidgets Inc.  All rights reserved.
 */

import com.phidgets.*;
import com.phidgets.event.*;

public class ProjectPhidgets
{
	public final static String IDLE = "Sign in to use";
	public final static String WELCOME = "Hello ";
	public final static String PROGRAM_1 = " 1";
	public final static String PROGRAM_1_SELECT= ">1";
	public final static String PROGRAM_2 = " 2";
	public final static String PROGRAM_2_SELECT= ">2";
	public final static String PROGRAM_3 = " 3";
	public final static String PROGRAM_3_SELECT= ">3";
	public final static String BAD_FORM = "Steady Now!";
	public final static String OKAY_FORM = "Nice Job!";
	public final static String GOOD_FORM = "Almost There";
	public final static String LOWER_WEIGHTS = "Lower weightage";
	public final static String RAISE_WEIGHTS = "Raise weightage";
	public final static String SIGNING_OUT = "Goodbye";
	public final static String PHONE_FOUND = "Connected...";
	public final static String SENDING_DATA = "Uploading...";
	public static int currentSelectedProgram = 0;
	public static boolean onMenu = true;

	
	public static final void main(String args[]) throws Exception {
		InterfaceKitPhidget ik;
		TextLCDPhidget lcd;

        lcd = new TextLCDPhidget();
		//Example of enabling logging.
		//Phidget.enableLogging(Phidget.PHIDGET_LOG_VERBOSE, null);

		System.out.println(Phidget.getLibraryVersion());

		ik = new InterfaceKitPhidget();
		ik.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("attachment of " + ae);
				
			}
		});
		ik.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
			}
		});
		ik.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println(ee);
			}
		});
		ik.addInputChangeListener(new InputChangeListener() {
			public void inputChanged(InputChangeEvent oe) {
				System.out.println(oe);
			
			}
		});
		ik.addOutputChangeListener(new OutputChangeListener() {
			public void outputChanged(OutputChangeEvent oe) {
				System.out.println(oe);
			}
		});
		ik.addSensorChangeListener(new SensorChangeListener() {
			public void sensorChanged(SensorChangeEvent se) {
				//System.out.println(se);
				//System.out.println("This one changes");
				int sensor = se.getIndex();
				int value = se.getValue();
				if (sensor == 7){
					if (value > 900)
					{
						try {
							lcd.setDisplayString(1, " 1        2       >3");
						} catch (PhidgetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else if (value < 100)
					{
						try {
							lcd.setDisplayString(1, ">1        2        3");
						} catch (PhidgetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else if (value < 550 && value > 450){
						try {
							lcd.setDisplayString(1, " 1       >2        3");
						} catch (PhidgetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			}
		});

		ik.openAny();
		lcd.openAny();
		System.out.println("waiting for InterfaceKit attachment...");
		ik.waitForAttachment();
		
		ik.setSensorChangeTrigger(7, 50);
		
		Thread.sleep(500);
		
		if (lcd.getDeviceID() == TextLCDPhidget.PHIDID_TEXTLCD_ADAPTER) {
            System.out.println("# Screens: " + lcd.getScreenCount());

            //set screen 0 as active
            lcd.setScreen(0);
            lcd.setScreenSize(lcd.PHIDGET_TEXTLCD_SCREEN_2x16);
            lcd.initialize();

        }

        lcd.setDisplayString(0, "Hello World");
        lcd.setDisplayString(1, ">1        2        3");
        
        for (int i = 0; i < 100; i++) {
            lcd.setContrast(i);
            Thread.sleep(50);
        }

        
	
		if (ik.getInputCount() > 8)
			System.out.println("input(7,8) = (" +
			  ik.getInputState(7) + "," +
			  ik.getInputState(8) + ")");
		

		
		for (int j = 0; j < 1000 ; j++) {
			for (int i = 0; i < ik.getOutputCount(); i++) {
				ik.setOutputState(i, true);
			}
			for (int i = 0; i < ik.getOutputCount(); i++)
				ik.setOutputState(i, false);
		}
		

		System.out.println("Outputting events for 30 seconds.");
		Thread.sleep(30000);
		System.out.print("closing...");
		ik.close();
		ik = null;
		System.out.println(" ok");
		
	}
}
