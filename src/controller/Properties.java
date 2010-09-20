package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * A class that contains a table of properties
 * @author Frank Su
 * @author Stephen Gream
 *
 */
public class Properties
{
	private final static Hashtable<String,String> properties = new Hashtable<String,String>();
	private static String propFile = "Properties.txt";
	
	public static String filePathStr = "";	
	
	//TODO
	// Path to directory for storing graph states
//	private final String graphOutputsPath = "GraphOutputs/";
//	private final String gtsRulesPath = "GTSRules.ggx";
//	private final String gtsRulesSeqPath = "GTSRulesSeq.xml";
	
	// menu: Properties -> print...
	/**
	 * Read in a properties file
	 */
	public synchronized static void readProperties() throws FileNotFoundException {
		Enumeration<String> keys = properties.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			properties.remove(key);
		}
		Scanner sc = new Scanner(new File(propFile));
		String lineStr = "";
		String content[];
		try {
			//first use a Scanner to get each line
			while(sc.hasNextLine()) {
				lineStr = sc.nextLine();
				content = lineStr.split(":");
				properties.put(content[0], content[1]);
			}
		}
		finally {
			//ensure the underlying stream is always closed
			sc.close();
		}
	}
	
	/**
	 * Read in a properties file
	 * @param filePath Path to the properties file to be read
	 */
	public synchronized static void readProperties(String filePath) throws FileNotFoundException {
		Enumeration<String> keys = properties.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			properties.remove(key);
		}
		propFile = filePath;
		Scanner sc = new Scanner(new File(filePath));
		String lineStr = "";
		String content[];
		try {
			//first use a Scanner to get each line
			while(sc.hasNextLine()) {
				lineStr = sc.nextLine();
				content = lineStr.split(":");
				properties.put(content[0], content[1]);
			}
		}
		finally {
			//ensure the underlying stream is always closed
			sc.close();
		}
	}

	/**
	 * Write in new properties file
	 * @throws FileNotFoundException
	 */
	public synchronized static void rewritePropertiesFile() throws FileNotFoundException {
		// Create file 
		FileWriter fstream;
		String towrite = "";
		
		try {
			fstream = new FileWriter(propFile);
			BufferedWriter out = new BufferedWriter(fstream);
			//towrite = "PrintRules:" + printRules + "\nPrintGraphs:" + printGraphs + "\nPrintDebug:" + printDebug; 
			Enumeration<String> keys = properties.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				towrite = towrite + key + ":" + properties.get(key) + "\n";
//				System.out.printf("Wrote %s:%s\n",key,properties.get(key));
			}
			out.write(towrite);
			//Close the output stream
			out.close();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Get a property from the table
	 * @param property The property name
	 * @return The property value
	 */
	public synchronized static String getProperty(String property){
		return properties.get(property);
	}
	
	/**
	 * Set a property in the table
	 * @param property The property name
	 * @param value The value of the property as a string
	 */
	public synchronized static void setProperty(String property, String value){
		//Not sure if these need to have their accesses controlled, but it's better safe than sorry
		properties.remove(property);
		properties.put(property, value);
	}
}
