package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * A class that contains 3 static Boolean flags to turn on/off debug/status messages in the console.
 * @author Frank Su
 *
 */
public class Properties
{
	public static Boolean printRules, printGraphs, printDebug;

	// menu: Properties -> print...
	public static void readProperties() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("Properties.txt"));
		String lineStr = "";
		String content[];
		try {
			//first use a Scanner to get each line
			while(sc.hasNextLine()) {
				lineStr = sc.nextLine();
				content = lineStr.split(":");

				if(lineStr.startsWith("PrintRules")) {
					printRules = Boolean.parseBoolean(content[1]);
				} else if(lineStr.startsWith("PrintGraphs")) {
					printGraphs = Boolean.parseBoolean(content[1]);
				}
				else {
					printDebug = Boolean.parseBoolean(content[1]);
				}
			}
		}
		finally {
			//ensure the underlying stream is always closed
			sc.close();
		}
	}


	public static void rewritePropertiesFile() throws FileNotFoundException {
		// Create file 
		FileWriter fstream;
		String towrite = "";
		
		try {
			fstream = new FileWriter("Properties.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			towrite = "PrintRules:" + printRules + "\nPrintGraphs:" + printGraphs + "\nPrintDebug:" + printDebug; 
			
			out.write(towrite);
			//Close the output stream
			out.close();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
