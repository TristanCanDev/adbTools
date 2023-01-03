package adbTools;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * Master util class for ADB Handling
 * 
 * @author Tristan Bouchard
 * @version 0.4.0
 *
 */
public class PlatformTools {
	private String adbPath;

	/**
	 * 
	 * Class constructor that sets up path to ADB
	 * 
	 * @param toAdb String path to ADB
	 * 
	 */
	public PlatformTools(String toAdb) {
		File file = new File(toAdb);
		if (file.exists()) {
			adbPath = toAdb;
		} else {
			System.out.println("File does not exist");
		}
	}

	/**
	 * 
	 * Class constructor that sets ADB path as "adb"
	 * Only use this if you have ADB installed to C:/Windows/ or somewhere that allows it directly in the command line on other operating systems
	 * 
	 */
	public PlatformTools() {
		adbPath = "adb";
	}

	/**
	 * 
	 * Queries ADB for the currently connected devices and returns a list of them
	 * 
	 * @return A list of Device objects
	 * 
	 */
	public List<Device> getDevices() {
		Command adbDevices = new Command(adbPath, "devices", "-l");
		adbDevices.exec();

		List<Device> devices = new ArrayList<Device>();

		for (int i = 1; i < adbDevices.getOutput().size(); i++) {
			String line = adbDevices.getOutput().get(i);

			if (!line.equals("") && !line.equals(" ") && !line.equals("\n")) {
				String serial = line.substring(0, line.indexOf(" ") + 1).replace(" ", "");
				line = line.replace("         ", "").replace(serial, "");

				String state = line.substring(0, line.indexOf(" ") + 1).replace(" ", "");
				line = line.replace((state + " "), "").replace(" ", "");

				String model = line.substring(line.indexOf("model:"), line.indexOf("device:")).replace("model:", "");
				line = line.replace("model:" + model, "");

				int transId = Integer
						.parseInt(line.substring(line.indexOf("transport_id:")).replace("transport_id:", ""));

				Device device = new Device(serial, state, model, transId, adbPath);
				devices.add(device);
			}
		}
		return devices;
	}
	
	/**
	 * 
	 * A getter method for the adbPath field
	 * 
	 * @return A string path to an ADB installation
	 * 
	 */
	public String getAdbPath() {
		return adbPath;
	}
}
