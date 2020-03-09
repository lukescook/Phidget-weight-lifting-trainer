/*
 * Copyright 2007 Phidgets Inc.  All rights reserved.
 */

import com.phidgets.*;
import com.phidgets.event.*;

import java.util.ArrayList;

public class RFIDExample
{
	static User user;
	public static final void main(String args[]) throws Exception {
		RFIDPhidget rfid;

		System.out.println(Phidget.getLibraryVersion());

		rfid = new RFIDPhidget();
		rfid.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae)
			{
				try
				{
					((RFIDPhidget)ae.getSource()).setAntennaOn(true);
					((RFIDPhidget)ae.getSource()).setLEDOn(true);
				}
				catch (PhidgetException ex) { }
				System.out.println("attachment of " + ae);
			}
		});
		rfid.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
			}
		});
		rfid.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("error event for " + ee);
			}
		});
		rfid.addTagGainListener(new TagGainListener()
		{
			public void tagGained(TagGainEvent oe)
			{
				System.out.println("Tag Gained: " +oe.getValue() + " (Proto:"+ oe.getProtocol()+")");
				user = FileReader.findUser(oe.getValue());
				ArrayList<Session> ses = user.getSessions();
				String list = user.getOTHERNAMES() + " " + user.getSURNAMES() + "\n";
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
			}
		});
		rfid.addTagLossListener(new TagLossListener()
		{
			public void tagLost(TagLossEvent oe)
			{
				System.out.println(oe);
			}
		});
		rfid.addOutputChangeListener(new OutputChangeListener()
		{
			public void outputChanged(OutputChangeEvent oe)
			{
				System.out.println(oe);
			}
		});

		rfid.openAny();
		System.out.println("waiting for RFID attachment...");
		rfid.waitForAttachment(1000);

		System.out.println("Serial: " + rfid.getSerialNumber());
		System.out.println("Outputs: " + rfid.getOutputCount());
		
		//How to write a tag:
		//rfid.write("A TAG!!", RFIDPhidget.PHIDGET_RFID_PROTOCOL_PHIDGETS, false); 

		System.out.println("Outputting events.  Input to stop.");
		System.in.read();
		System.out.print("closing...");
		rfid.close();
		rfid = null;
		System.out.println(" ok");
		if (false) {
			System.out.println("wait for finalization...");
			System.gc();
		}
	}
}
