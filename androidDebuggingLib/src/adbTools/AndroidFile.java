package adbTools;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * A class built to represent files and directories on a device
 * 
 * @author Tristan Bouchard
 * @version 0.4.0
 *
 */
public class AndroidFile {
	private String pathToFile;
	private String fileExtension;
	
	/**
	 * 
	 * Class constructor for an AndroidFile
	 * 
	 * @param path The path to the file or directory on a device
	 * 
	 */
	public AndroidFile(String path) {
		pathToFile = path;
		
		Pattern p = Pattern.compile("\\.\\w+$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(path);
		
		if(m.find()) {
			fileExtension = path.replace(path.split("\\.\\w+$")[0], "");
		}
	}
	
	/**
	 * 
	 * A method to check if this is a file or directory
	 * 
	 * @return A boolean showing whether or not this is a file
	 * 
	 */
	public boolean isFile() {
		Pattern p = Pattern.compile("\\.\\w+$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(pathToFile);
		return m.find();
	}
	
	/**
	 * 
	 * A method to check if this is a file or directory
	 * 
	 * @return A boolean showing whether or not this is a directory
	 * 
	 */
	public boolean isDir() {
		return !this.isFile();
	}
	
	/**
	 * 
	 * A method that returns the extension of a file
	 * 
	 * @return If it is a file, a String extension (such as .apk or .txt)
	 * 
	 */
	public String getExtension() {
		if(this.isFile()) {
			return fileExtension;
		}
		else {
			return null;
		}
	}
	
	/**
	 * 
	 * A method to get the file or directory name
	 * 
	 * @return A string name of the file or directory
	 * 
	 */
	public String getFileName() {
		if(this.isFile()) {
			return pathToFile.replace(pathToFile.split("\\w+\\.\\w+$")[0], "");
		}else {
			Pattern p = Pattern.compile("\\/\\w+$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(pathToFile);
			if(m.find()) {
				return pathToFile.replace(pathToFile.split("\\/\\w+$")[0], "").replace("/", "");
			}
			else {
				return pathToFile.replace(pathToFile.split("\\/\\w+\\/$")[0], "").replace("/", "");
			}
		}
	}
	
	/**
	 * 
	 * A method to check if the file exists on a given device
	 * 
	 * @param device The device to check if the file exists on
	 * @return A boolean value showing whether or not the file exists
	 * 
	 */
	public boolean exists(Device device) {
		List<String> shellOut = device.shell("ls" + pathToFile);
		boolean doesExist = true;
		for(int i = 0; i < shellOut.size(); i++) {
			if(shellOut.get(i).contains("No such file or directory") || shellOut.get(i).contains("inaccessible or not found")) {
				doesExist = false;
			}
		}
		return doesExist;
	}
	
	/**
	 * 
	 * A method to pull this file from a device
	 * 
	 * @param device The device to pull this file from
	 * @param localPath The local path to put the file
	 * @throws FileNotFoundException When the local file could not be found
	 * 
	 */
	public void pull(Device device, String localPath) throws FileNotFoundException {
		device.pull(this, localPath);
	}
	
	/**
	 * 
	 * A method that returns the representation of this file as a string
	 * 
	 */
	public String toString() {
		return pathToFile;
	}
	
	/**
	 * 
	 * A method to compare a file with another
	 * 
	 * @param file The file to compare with
	 * @return Whether or not the files are the same
	 * 
	 */
	public boolean equals(AndroidFile file) {
		return pathToFile == file.pathToFile;
	}
}
