package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Debug {
	protected static PrintWriter debugger;
	protected static boolean debuggerActive = false;
	public static void log() {
		log("");
	}
	public static void logOpenSection(String sect) {
		log("------- " + sect + " ------");
	}
	public static void logCloseSection() {
		log("------------\n");
	}
	public static void log(String l) {
		if(!debuggerActive) {
			activateDebugger();
		}
		debugger.println(l);
		debugger.flush();
	}
	protected static void activateDebugger() {

		try {
			debugger = new PrintWriter(Game.getWorkingDirectory() + "DebugLog.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Debug log initialization failed");
		}
		debuggerActive = true;
	}
	

	public static void checkDirectoryAvailable() {
		// Check main directory exists
		File baseDir = new File(Game.getWorkingDirectory());
		File[] subfiles = baseDir.listFiles();
		if(subfiles == null) {
			log("Base directory is not properly defined or access is not given");
			throw new RuntimeException("Base directory is not properly defined or access is not given");
		}
		// Check subdirectories exists
		String[] neededFiles = {"saves","maps","instructions","spritepacks"};
		for(String loc : neededFiles) {
			String dir = baseDir.getPath() +'\\' + loc;
			if(!new File(dir).exists()) {
				log("Missing file at " + dir);
			}
		}
	}

}
