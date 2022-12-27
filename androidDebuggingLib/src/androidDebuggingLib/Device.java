package androidDebuggingLib;

import java.util.List;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * This is a utility class that models an Android Device and allows you to
 * execute ADB commands on the device
 *
 */
public class Device {
	private String serialNo;
	private String model;
	private String adbPath;
	private String state;
	private int transportId;

	/**
	 * 
	 * A device constructor to fill in the data returned by "adb devices -l"
	 * 
	 * @param setSerial  The serial number for the Device
	 * @param setState   The state of the device (offline, device, etc)
	 * @param setModel   The model of the device (Such as 'Quest_2')
	 * @param setTransId The transport ID for the device
	 * @param setAdbPath The path to ADB on the system
	 * 
	 */
	public Device(String setSerial, String setState, String setModel, int setTransId, String setAdbPath) {
		serialNo = setSerial;
		state = setState;
		model = setModel;
		transportId = setTransId;
		adbPath = setAdbPath;
	}

	/**
	 * 
	 * A device constructor to fill in the data returned by "adb devices -l" this
	 * constructor pulls from a list of devices and initializes as that device
	 * 
	 * @param i The location of the device in the list of Connected Devices
	 * 
	 */
	public Device(int i) {
		PlatformTools pfTools = new PlatformTools();
		List<Device> devices = pfTools.getDevices();

		if (devices.size() >= i) {
			Device device = devices.get(i);

			serialNo = device.serialNo;
			model = device.model;
			adbPath = device.adbPath;
			state = device.state;
			transportId = device.transportId;
		}
	}

	/**
	 * 
	 * A device constructor to fill in the data returned by "adb devices -l" this
	 * constructor pulls the first device from the list of connected devices and
	 * initializes as that device
	 * 
	 */
	public Device() {
		PlatformTools pfTools = new PlatformTools();
		List<Device> devices = pfTools.getDevices();

		if (devices.size() > 0) {
			Device device = devices.get(0);

			serialNo = device.serialNo;
			model = device.model;
			adbPath = device.adbPath;
			state = device.state;
			transportId = device.transportId;
		}
	}

	public String toString() {
		return String.format("Serial: %s, Model: %s, State: %s, Transport ID: %d", serialNo, model, state, transportId);
	}

	public String getSerial() {
		return serialNo;
	}

	public String getModel() {
		return model;
	}

	public String getState() {

		PlatformTools pfTools = new PlatformTools();
		List<Device> devices = pfTools.getDevices();

		int index = -1;
		for (int i = 0; i < devices.size(); i++) {
			if (devices.get(i).getSerial().equals(serialNo)) {
				state = devices.get(i).getState();
				index = i;
			}
		}
		if (index < 0) {
			state = "offline";
		}
		return state;
	}

	public int getTransportId() {
		return transportId;
	}

	/**
	 * 
	 * A method to push a local file or directory to the device
	 * 
	 * @param localPath  The path to either the directory or the file
	 * @param remotePath The path to place the local file on the device (for example
	 *                   'sdcard/android/data')
	 * 
	 */
	public void push(String localPath, String remotePath) {
		File file = new File(localPath);
		if (file.exists()) {
			Path path = Paths.get(file.getAbsolutePath());

			Command pushFile = new Command(adbPath, "-s", serialNo, "push", file.getAbsolutePath());
			if (file.isDirectory()) {
				pushFile.addArg(remotePath);
				pushFile.exec();
			} else if (file.isFile()) {
				pushFile.addArg(remotePath + "/" + path.getFileName());
				pushFile.exec();
			}

			for (int i = 0; i < pushFile.getOutput().size(); i++) {
				String line = pushFile.getOutput().get(i);
				if (line.contains("No such file or directory")) {
					System.out.println("Push Failed: No such file or directory on device");
				}
			}
		} else {
			System.out.println("File does not exist");
		}
	}
}
