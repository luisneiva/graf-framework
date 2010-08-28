package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Properties
{
	public static Boolean printRules, printGraphs, printDebug;

	// NEW menu -> properties -> print...
	public static void readProperties() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("Properties.txt"));
		String lineStr = "";
		String content[];
		try {
			//first use a Scanner to get each line
			while(sc.hasNextLine()) {
				lineStr = sc.nextLine();
				content = lineStr.split(" ");
				
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
	
	
}