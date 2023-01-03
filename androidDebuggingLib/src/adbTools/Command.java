package adbTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Command Builder
 * 
 * @author Tristan Bouchard
 * @version 0.4.0
 *
 */
public class Command {
	private String command;
	private List<String> args = new ArrayList<String>();
	private List<String> output = new ArrayList<String>();

	/**
	 * 
	 * Class constructor. Sets command and arguments.
	 * 
	 * @param setCommand A string value to set as a command
	 * @param setArgs    An existing String list of arguments
	 * 
	 */
	public Command(String setCommand, List<String> setArgs) {
		command = setCommand;
		args = setArgs;
	}

	/**
	 * 
	 * Class constructor. Sets commands and adds arguments if specified.
	 * 
	 * @param setCommand A string value to set as a command
	 * @param arguments  String values to add as arguments
	 * 
	 */
	public Command(String setCommand, String... arguments) {
		command = setCommand;

		if (arguments.length > 0) {
			for (int i = 0; i < arguments.length; i++) {
				args.add(arguments[i]);
			}
		}
	}

	/**
	 * 
	 * Getter method for the Command field
	 * 
	 * @return Initial Command String
	 * 
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 
	 * Getter method for the output of a command
	 * 
	 * @return A list of output lines after the execution of a command
	 * 
	 */
	public List<String> getOutput() {
		return output;
	}
	
	/**
	 * 
	 * Getter method for the output of a command
	 * 
	 * @param i An integer index for the output lines
	 * @return A String line from the output of a command
	 * 
	 */
	public String getOutput(int i) {
		if(output.size() >= i) {
			return output.get(i);
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * 
	 * Getter method for the Args field
	 * 
	 * @return String list of arguments
	 * 
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * Override of the natural toString() method
	 * 
	 * @return String representation of this class containing the command and
	 *         arguments
	 * 
	 */
	public String toString() {
		String arguments = "";

		if (args.size() > 0) {
			for (int i = 0; i < args.size(); i++) {
				arguments += (" " + args.get(i));
			}

			return String.format("%s %s", command, arguments);
		} else {
			return command;
		}
	}

	/**
	 * 
	 * Setter method for the command field
	 * 
	 * @param toSet String command to set as a command
	 * 
	 */
	public void setCommand(String toSet) {
		command = toSet;
	}

	/**
	 * 
	 * Setter method for the args field
	 * 
	 * @param toSet A string list to set as arguments
	 * 
	 */
	public void setArgs(List<String> toSet) {
		args = toSet;
	}

	/**
	 * 
	 * Setter method for the args field.
	 * 
	 * @param toAdd String argument to add to argument field
	 * 
	 */
	public void addArg(String toAdd) {
		args.add(toAdd);
	}

	/**
	 * 
	 * Setter method for the args field.
	 * 
	 * @param toAdd Multiple string arguments to add to argument field
	 * 
	 */
	public void addArgs(String... toAdd) {
		for (int i = 0; i < toAdd.length; i++) {
			args.add(toAdd[i]);
		}
	}

	/**
	 * 
	 * Executes the given command and adds line by line output to output field
	 * 
	 */
	public void exec() {
		try {
			Process process = Runtime.getRuntime().exec(this.toString());

			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errOut = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			String line = out.readLine();
			while (line != null) {
				output.add(line);
				line = out.readLine();
			}
			
			line = errOut.readLine();
			while(line != null) {
				output.add(line);
				line = errOut.readLine();
			}

			errOut.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
