/*
 * Copyright 2007 Phidgets Inc.  All rights reserved.
 */

import java.util.ArrayList;
import com.phidgets.*;
import com.phidgets.event.*;

import com.phidget22.Accelerometer;
import com.phidget22.AccelerometerAccelerationChangeEvent;
//import com.phidget22.AccelerometerAccelerationChangeListener;



public class ProjectPhidgets
{

	//Messages
	public final static String IDLE = "Sign in to use";
	public final static String WELCOME = "Hello, ";
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
	public final static String BEGIN = "Let's Begin!";
	public final static String SIGNING_OUT = "Goodbye";
	public final static String NEXT_SET = "Next Set!";
	public final static String SWIPE_CARD = "Swipe Card";
	public final static String BEGIN_SESSION = (ZER0 + SCREEN_SPACE + ZER0 + SCREEN_SPACE + ZER0);
	
	
	//Timings
	public final static int DELAY_BETWEEN_USERS = 4000;
	public final static int RUN_TIME= 120000;
	
	//Ports
	public final static int SENSOR_SLIDER_PORT = 7;
	public final static int SENSOR_FORCE_PORT = 4;
	public final static int SENSOR_EXIT_BUTTON_PORT = 0;
	public final static int SENSOR_SELECT_BUTTON_PORT = 2;

	//Other variables
	public static boolean onMenu = true;
	public static boolean allowForceReading = true;
	public static boolean reachedTop = false;
	public static boolean reachedBottom = true; //Default position
	public static boolean movingDown = false;
	public static boolean makeNewForm = false;
	public static int currentWeight = 10;  //10 As that is typically the defailt lowest weight on a machine
	public static int currentSelectedProgram = 0;
	public static int goodForm;
	public static int okayForm;
	public static int badForm;
	public static int repCounter = 0;
	public static int numJitters = 0;
	public static int accelerometerFormClass = 0;
	public static int forceFormClass = 0;
	public static int servoStartPos = 220;
	public static int servoEndPos = 0;
	public static double lastMotorPos = servoStartPos;
	public static double lastX = 0.0;
	public static double lastY = 0.0;
	public static String loggedInUser;

	
	//Interfaces - Phidgets
	public static TextLCDPhidget lcd;
	public static InterfaceKitPhidget ik;
	public static ServoPhidget servo;
	public static RFIDPhidget rfid;
	public static Accelerometer accelerometer;
	//Interfaces - Other
	public static FileReader reader;
	public static FileWriter writer;
	public static Session thisSession;
	public static User currentUser = null;
	
	
	public static final void main(String args[]) throws Exception {
		//Initialise Interfaces
		
		rfid = new RFIDPhidget();
		servo = new ServoPhidget();							
        lcd = new TextLCDPhidget();
		ik = new InterfaceKitPhidget();
		accelerometer = new Accelerometer();
		
		//***********************************************************INTERFACE KIT LISTENERS
		ik.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("Interface Kit Attached");
				
			}
		});
		ik.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("Interface Kit Detached");
			}
		});
		ik.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("Interface Kit Error");
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
				//***************************************************************************SLIDER
				if (sensor == SENSOR_SLIDER_PORT){
					if(onMenu){
						if (value > 800)
						{
							try {
								lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3_SELECT);
								currentSelectedProgram = 3;
							} catch (PhidgetException e) {
								e.printStackTrace();
							}
							
						}
						else if (value < 200)
						{
							try {
								lcd.setDisplayString(1, PROGRAM_1_SELECT + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3);
								currentSelectedProgram = 1;
							} catch (PhidgetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						else if (value < 600 && value > 400){
							try {
								lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2_SELECT + SCREEN_SPACE + PROGRAM_3);
								currentSelectedProgram = 2;
							} catch (PhidgetException e) {
								e.printStackTrace();
							}
							
						}
					}
					
				}
				//***************************************************************************FORCE
				if (sensor == SENSOR_FORCE_PORT) {
					
					if (value >= 700 && allowForceReading == true) {		//*******BAD FORM
						try {
							allowForceReading = false;
							forceFormClass = 1;
							//lcd.setDisplayString(0, BAD_FORM);
							
							
							checkClasses();
							//badForm++;
							//thisSession.incrementForWeightBad(currentWeight);
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value > 450 && value < 700 && allowForceReading == true) {		//*******OKAY FORM
						try {
							
							allowForceReading = false;
							forceFormClass = 2;
							//lcd.setDisplayString(0, OKAY_FORM);
							
							checkClasses();
							//okayForm++;
							//thisSession.incrementForWeightOkay(currentWeight);
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value > 200 && value < 450 && allowForceReading == true) {		//*******GOOD FORM
						try {
							allowForceReading = false;
							forceFormClass = 3;
							//lcd.setDisplayString(0, GOOD_FORM);
							
							checkClasses();
							//goodForm++;
							//thisSession.incrementForWeightGood(currentWeight);
							updateDisplay();
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
						
					}
					else if (value < 50 && allowForceReading == false) {
						allowForceReading = true;
						
					}
				}
				//***************************************************************************BUTTON
				if (sensor == SENSOR_SELECT_BUTTON_PORT) {
					if (value < 500)
					{
						try {
							if(onMenu) {
								//loggedInUser = "0102388fe2.txt";	//Uncomment if cannot use RFID and using example
								currentUser = FileReader.findUser(loggedInUser);
								if(currentUser == null) {
									lcd.setDisplayString(0, SWIPE_CARD);
								}
								else {
									onMenu = false;
									thisSession = currentUser.addNewSession();
									
									lcd.setDisplayString(0, BEGIN);
									lcd.setDisplayString(1, BEGIN_SESSION);
									goodForm = 0;
									okayForm = 0;
									badForm = 0;
									repCounter = 0;
								}
							}
						} catch (PhidgetException e) {
							e.printStackTrace();
						}
					}
					
					
				}
				
				//***************************************************************************LIGHT
				if (sensor == SENSOR_EXIT_BUTTON_PORT) {
					if (value < 200)  		//Uncomment in daytime
					//if (value < 50)				//Uncomment in nighttime
					{
						try {
							if(!onMenu){
								lcd.setDisplayString(0, SIGNING_OUT);
								servo.setPosition(0, servoStartPos);	
								//Save data from this session
								ArrayList<Session> ses = currentUser.getSessions();
								String list = currentUser.getOTHERNAMES() + " " + currentUser.getSURNAMES() + "\n";
								for (Session elem : ses) {
									Integer[] val = elem.getAllWeightValues();
									if(val.length != 0) {
										list += elem.getDateWrite() + ",";
										for (Integer form : val) {
											list += form + "," + elem.getFormForWeight(form)[0] + "," + elem.getFormForWeight(form)[1] + "," + elem.getFormForWeight(form)[2] + ";";
										}
										list = list.replaceAll(".$", "\n");
									}
								}
								System.out.println(list.trim());
								//writer.writeInfo("01069351f0write.txt", currentUser);			//For testing purposes
								writer.writeInfo(loggedInUser, currentUser);	
								
								//Reset data
								thisSession = null;
								loggedInUser = null;
								
								
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
			}
		});
		
		//***********************************************************SERVO LISTENERS
		servo.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("Servo Attached");
			}
		});
		servo.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("Servo Detached");
			}
		});
		servo.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("Servo Error");
			}
		});
		
		//***********************************************************ACCELEROMETERLISTENERS
		accelerometer.addAccelerationChangeListener(new com.phidget22.AccelerometerAccelerationChangeListener() {
			public void onAccelerationChange(AccelerometerAccelerationChangeEvent e) {
				//System.out.println(e.getAcceleration()[0] + "  " + e.getAcceleration()[2]);
				if((e.getAcceleration()[0] < 0.15 && e.getAcceleration()[2] > 0.8) || (e.getAcceleration()[0] > -0.2 && e.getAcceleration()[2] > 0.9)) {
					System.out.println("Acc reached bottom");
					reachedBottom =true;
					
					if (movingDown) {
						if(numJitters < 2){
							accelerometerFormClass = 3;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Good form");
						}
						else if(numJitters >= 2 && numJitters < 10) {
							accelerometerFormClass = 2;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Okay form");
						}
						else {
							accelerometerFormClass = 1;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Bad form");
						}
						
					}
					movingDown = false;	
					numJitters = 0;
					
				}
				else {
					reachedBottom =false;
				}
				
				
				if((e.getAcceleration()[0] > 0.9 && e.getAcceleration()[2] < 0.2) || (e.getAcceleration()[0] > 0.85 && e.getAcceleration()[2] > -0.3)){
					System.out.println("Acc reached top");
					reachedTop = true;
					makeNewForm = true;
					//System.out.println("Reached Top = " + reachedTop);
					//If starting to move down, even a tiny bit
					if( e.getAcceleration()[0] < lastX &&  e.getAcceleration()[2] > lastY){
						movingDown = true;
					}
					
				}
				else {
					reachedTop =false;
				}
				
				if(movingDown){
					//If moved up at all
					if( e.getAcceleration()[0] > lastX  || e.getAcceleration()[2] < lastY){
						numJitters++;
						//System.out.println("Number of Jitters = " + numJitters);
					}
					
				}
				lastX = e.getAcceleration()[0];
				lastY = e.getAcceleration()[2];
				
			}
				/*if(e.getAcceleration()[0] < 0.1 && e.getAcceleration()[2] > 0.8) {
					reachedBottom =true;
					//System.out.println("Reached Bottom = " + reachedBottom);
					
					if (movingDown) {
						if(numJitters < 2) {
							System.out.println("Good form");
							accelerometerFormClass = 3;
						}
						else if(numJitters >= 2 && numJitters < 10) {
							System.out.println("Okay form");
							accelerometerFormClass = 2;
						}
						else {
							System.out.println("Bad form");
							accelerometerFormClass = 1;
						}
						//These were outside of the } just below, probably there for a reason but let's see
						movingDown = false;	
						numJitters = 0;
					}
					
				}
				else {
					reachedBottom =false;
				}
				
				
				
				
				if(e.getAcceleration()[0] > 0.7 && e.getAcceleration()[2] < 0.6) {
					reachedTop = true;
					//If starting to move down, even a tiny bit
					if( e.getAcceleration()[0] < lastX &&  e.getAcceleration()[2] > lastY) {
						movingDown = true;
					}
				}
				else {
					reachedTop =false;
				}
				
				if(movingDown) {
					//If moved up at all
					if( e.getAcceleration()[0] > lastX  || e.getAcceleration()[2] < lastY) {
						numJitters++;
						System.out.println("Number of Jitters = " + numJitters);
					}	
				}
				
				lastX = e.getAcceleration()[0];
				lastY = e.getAcceleration()[2];
			}*/
		});

		accelerometer.addAttachListener(new com.phidget22.AttachListener() {
			public void onAttach(com.phidget22.AttachEvent arg0) {
				System.out.println("Accelerometer Attached");
			}
		});
		accelerometer.addDetachListener(new com.phidget22.DetachListener() {
			public void onDetach(com.phidget22.DetachEvent arg0) {
				System.out.println("Accelerometer Detached");
			}
		});
		
		/*accelerometer.addAccelerationChangeListener(new AccelerometerAccelerationChangeListener() {
			public void onAccelerationChange(AccelerometerAccelerationChangeEvent e) {
				
				if(e.getAcceleration()[0] < 0.1 || e.getAcceleration()[2] > 0.8) {
					reachedBottom =true;
					
					if (movingDown) {
						System.out.println("Acc reached bottom");
						if(numJitters < 2){
							accelerometerFormClass = 3;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Good form");
						}
						else if(numJitters >= 2 && numJitters < 10) {
							accelerometerFormClass = 2;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Okay form");
						}
						else {
							accelerometerFormClass = 1;
							
							try{
								checkClasses();
							} catch (PhidgetException e1)
							{ e1.printStackTrace();}
							
							//System.out.println("Bad form");
						}
						
					}
					movingDown = false;	
					numJitters = 0;
					
				}
				else {
					reachedBottom =false;
				}
				
				
				if(e.getAcceleration()[0] > 0.9 || e.getAcceleration()[2] < 0.2){
					System.out.println("Acc reached top");
					reachedTop = true;
					makeNewForm = true;
					//System.out.println("Reached Top = " + reachedTop);
					//If starting to move down, even a tiny bit
					if( e.getAcceleration()[0] < lastX &&  e.getAcceleration()[2] > lastY){
						movingDown = true;
					}
					
				}
				else {
					reachedTop =false;
				}
				
				if(movingDown){
					//If moved up at all
					if( e.getAcceleration()[0] > lastX  || e.getAcceleration()[2] < lastY){
						numJitters++;
						System.out.println("Number of Jitters = " + numJitters);
					}
						
				}
				lastX = e.getAcceleration()[0];
				lastY = e.getAcceleration()[2];
			}
		});*/
		

		
		//***********************************************************RFID LISTENERS
		rfid.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) 
			{
				System.out.println("RFID Reader Attached");
			}
		});
		rfid.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("RFID Reader Detached");
			}
		});
		rfid.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("RFID Reader Error");
			}
		});
		rfid.addTagGainListener(new TagGainListener()
		{
			public void tagGained(TagGainEvent oe)
			{
				System.out.println("RFID Tag scanned: " + oe);
				try {
					String reading = "";
					reading = oe.getValue();
					System.out.println("RFID Tag scanned: " + oe.getValue());
					if(onMenu) {
						//loggedInUser = "0102388fe2.txt";	//If cannot use RFID
						loggedInUser = reading + ".txt";
						currentUser = FileReader.findUser(loggedInUser);
						String welcomeName = currentUser.getOTHERNAMES();
						
						if(welcomeName.length() > 13){
							welcomeName = welcomeName.substring(0, 12);
						}
						
						lcd.setDisplayString(0, WELCOME + welcomeName);
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else {
						if(reading.equals("01069344f9") && !movingDown) {
							System.out.println("Using 40kg");
							currentWeight = 40;
						}
						else if(reading.equals("0106935feb") && !movingDown) {
							System.out.println("Using 50kg");
							currentWeight = 50;
						} else if(reading.equals("0106935ce9") && !movingDown) {
							System.out.println("Using 60kg");
							currentWeight = 60;
						}
					}
				} catch (PhidgetException e) {
					e.printStackTrace();
				}
			
			}
		});
		rfid.addTagLossListener(new TagLossListener() {
			public void tagLost(TagLossEvent oe) {
				System.out.println("RFID Tag removed: " + oe.getValue());
			}
		});
		
		//Open interfaces
		servo.openAny();
		ik.openAny();
		lcd.openAny();
		rfid.openAny();
		accelerometer.open();
		
		//Wait for attachments
		ik.waitForAttachment();
		lcd.waitForAttachment();
		rfid.waitForAttachment();
		servo.waitForAttachment();							

		
		//Any other settings that need to be adjusted
		servo.setPosition(0, servoStartPos);	
		//accelerometer.setAccelerationChangeTrigger(0.10);

		
		//LCS monitor needs a bit of time for it to properly set up, keep this sleep in
		Thread.sleep(100);
		
		if (lcd.getDeviceID() == TextLCDPhidget.PHIDID_TEXTLCD_ADAPTER) {
            lcd.setScreen(0);
            lcd.setScreenSize(TextLCDPhidget.PHIDGET_TEXTLCD_SCREEN_2x20);
            lcd.initialize();
        }

        lcd.setDisplayString(0, IDLE);
        lcd.setDisplayString(1, PROGRAM_1 + SCREEN_SPACE + PROGRAM_2 + SCREEN_SPACE + PROGRAM_3);
        lcd.setContrast(100);
       
       
       
		

		//System.out.println("Outputting events for 30 seconds.");
		//Thread.sleep(RUN_TIME);
        System.in.read();
        
        servo.setPosition(0, servoStartPos);	
        Thread.sleep(1000);
        servo.setEngaged(0, false);
        servo.close();
        servo = null;
		ik.close();
		ik = null;
		rfid.close();
		rfid = null;
		accelerometer.close();
		accelerometer = null;
		System.out.println("Finished Program");
	}
	
	public static void checkClasses() throws PhidgetException{
		if(makeNewForm){
			System.out.println("Acc: " + accelerometerFormClass + "\tFrc: " + forceFormClass);
			if(accelerometerFormClass != 0 && forceFormClass != 0){
				
				if(accelerometerFormClass == 1 || forceFormClass == 1){
					lcd.setDisplayString(0, BAD_FORM);
					badForm++;
					thisSession.incrementForWeightBad(currentWeight);
					updateDisplay();
				}
				else if(accelerometerFormClass == 2 || forceFormClass == 2){
					lcd.setDisplayString(0, OKAY_FORM);
					okayForm++;
					thisSession.incrementForWeightOkay(currentWeight);
					updateDisplay();
				}
				else if(accelerometerFormClass == 3 && forceFormClass == 3){
					lcd.setDisplayString(0, GOOD_FORM);
					goodForm++;
					thisSession.incrementForWeightGood(currentWeight);
					updateDisplay();
				}
				
				
				
				if(currentSelectedProgram == 1) {
					repCounter++;
					if(repCounter % 10 == 0){
						lastMotorPos = servoStartPos;
						servo.setPosition(0, lastMotorPos);
						lcd.setDisplayString(0, NEXT_SET);
						
					}else {
						lastMotorPos-=22;
						servo.setPosition(0, lastMotorPos);

					}
				}
				else if(currentSelectedProgram == 2) {
					repCounter++;
					if(repCounter % 20 == 0) {
						lastMotorPos = servoStartPos;
						servo.setPosition(0, lastMotorPos);
						lcd.setDisplayString(0, NEXT_SET);
					}else{
						lastMotorPos-=11;
						servo.setPosition(0, lastMotorPos);
				
					}
				}
				accelerometerFormClass = 0;
				forceFormClass = 0;
				makeNewForm = false;
			}
		}
		
	}
	
	//If number is 1 digit, add a space so it looks nice
	public static void updateDisplay()
	{
		String gForm = "";
		String oForm = "";
		String bForm = "";
		if(goodForm < 10) {
			gForm += " " + goodForm;
		}
		else {
			gForm += goodForm;
		}
		
		if(okayForm < 10) {
			oForm += " " + okayForm;
		}
		else{
			oForm += okayForm;
		}
		
		if(badForm < 10) {
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
