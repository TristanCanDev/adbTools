package adbTools;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.lang.IndexOutOfBoundsException;
import java.lang.RuntimeException;

/**
 * 
 * This is a utility class that models an Android Device and allows you to
 * execute ADB commands on the device
 * 
 * @author Tristan Bouchard
 * @version 0.3.0
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
		} else {
			throw new IndexOutOfBoundsException("No device could be found within that index");
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
		} else {
			throw new RuntimeException("There are no devices connected");
		}
	}

	/**
	 * 
	 * Returns a String representation of a device
	 * 
	 */
	public String toString() {
		return String.format("Serial: %s, Model: %s, State: %s, Transport ID: %d", serialNo, model, state, transportId);
	}

	/**
	 * 
	 * A getter method for the device serial number
	 * 
	 * @return A String serial number for an android device
	 * 
	 */
	public String getSerial() {
		return serialNo;
	}

	/**
	 * 
	 * A getter method for the device model
	 * 
	 * @return A string model name for an android device
	 * 
	 */
	public String getModel() {
		return model;
	}

	/**
	 * 
	 * A getter method for the state of the device
	 * 
	 * @return A string representation of the device state
	 * 
	 */
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

	/**
	 * 
	 * A getter method for the transport id of the device
	 * 
	 * @return An integer transport id for an android device
	 * 
	 */
	public int getTransportId() {
		return transportId;
	}
	
	public List<String> getPackages(){
		
		List<String> packages = new ArrayList<String>();
		Command pmShell = new Command(adbPath, "-s", serialNo, "shell", "pm", "list", "packages");
		pmShell.exec();
		
		for(int i = 0; i < pmShell.getOutput().size(); i++) {
			String line = pmShell.getOutput(i);
			if(!line.equals(" ") && !line.equals("") && line.startsWith("package:")) {
				packages.add(line.replace("package:", ""));
			}
		}
		
		return packages;
		
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
	public void push(String localPath, String remotePath) throws FileNotFoundException {
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
					throw new FileNotFoundException(
							"The file/directory specified in remotePath does not exist on the device");
				}
			}
		} else {
			throw new FileNotFoundException("The directory or file given does not exist");
		}
	}

	public void pull(String remotePath, String localPath) throws FileNotFoundException {
		File local = new File(localPath);
		File remote = new File(remotePath);
		if (local.exists()) {
			Command pullFile = new Command(adbPath, "-s", serialNo, "pull", remotePath);
			
			if(local.isDirectory()) {
				pullFile.addArg(localPath + "\\" + remote.getName());
				pullFile.exec();
			}else {
				pullFile.addArg(localPath);
				pullFile.exec();
			}
			for(int i = 0; i < pullFile.getOutput().size(); i++) {
				String line = pullFile.getOutput().get(i);
				if (line.contains("No such file or directory")) {
					throw new FileNotFoundException(
							"The file/directory specified in remotePath does not exist on the device");
				}
			}
		}
		else {
			throw new FileNotFoundException("The file/directory specified in localPath does not exist on this machine");
		}
	}
	
	public void pull(String remotePath, String localPath, boolean preserve) {
		File local = new File(localPath);
		File remote = new File(remotePath);
		if (local.exists()) {
			Command pullFile = new Command(adbPath, "-s", serialNo, "pull");
			
			if(preserve) {
				pullFile.addArg("-a");
			}
			
			if(local.isDirectory()) {
				pullFile.addArgs(remotePath, localPath + "\\" + remote.getName());
				pullFile.exec();
			}else {
				pullFile.addArgs(remotePath, localPath);
				pullFile.exec();
			}
		}
	}
	
	public void install(String packagePath) {
		File file = new File(packagePath);
		
		if(file.exists() && file.isFile()) {
			if(file.getName().endsWith(".apk")) {
				Command installPackage = new Command(adbPath, "-s", serialNo, "install", file.getAbsolutePath());
				installPackage.exec();
			}
		}
	}
	
	public void install(String packagePath, String...args) {
		File file = new File(packagePath);
		
		if(file.exists() && file.isFile()) {
			if(file.getName().endsWith(".apk")) {
				Command installPackage = new Command(adbPath, "-s", serialNo, "install");
				for(int i = 0; i < args.length; i++) {
					installPackage.addArg(args[i]);
				}
				installPackage.addArg(file.getAbsolutePath());
				installPackage.exec();
			}
		}
	}
	
	public void installMultiple(String...packagePaths) {
		List<File> packages = new ArrayList<File>();
		
		for(int i = 0; i < packagePaths.length; i++) {
			File file = new File(packagePaths[i]);
			packages.add(file);
		}
		
		Command installMultiple = new Command(adbPath, "-s", serialNo, "install-multiple");
		
		for(int i = 0; i < packages.size(); i++) {
			if(packages.get(i).isFile() && packages.get(i).getName().endsWith(".apk")) {
				installMultiple.addArg(packages.get(i).getAbsolutePath());
			}
		}
	}
	
	public void uninstall(String application) throws FileNotFoundException {
		Command uninstall = new Command(adbPath, "-s", serialNo, "uninstall", application);
		uninstall.exec();
		
		for(int i = 0; i < uninstall.getOutput().size(); i++) {
			if(uninstall.getOutput(i).contains("No such file or directory")) {
				throw new FileNotFoundException("The file/directory specified in remotePath does not exist on the device");
			}
		}
	}
	
	public void enableVerity() {
		Command enable = new Command(adbPath, "-s", serialNo, "enable-verity");
		enable.exec();
	}
	
	public void disableVerity() {
		Command disable = new Command(adbPath, "-s", serialNo, "disable-verity");
		disable.exec();
	}
	
	public void reboot(String option) {
		Command reboot = new Command(adbPath, "-s", serialNo, "reboot");
		if(option.equals("bootloader") || option.equals("recovery") || option.equals("sideload") || option.equals("sideload-auto-reboot")) {
			reboot.addArg(option);
			reboot.exec();
		}
		else {
			throw new RuntimeException("The option specified does not work with this command. Try either sideload, recovery, bootloader, or sideload-auto-reboot");
		}
	}
}
