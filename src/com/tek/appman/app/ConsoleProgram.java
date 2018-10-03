package com.tek.appman.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONObject;

import com.tek.appman.fx.controllers.MainController;

public class ConsoleProgram {
	
	private String name, relativeJarPath, arguments;
	private boolean running;
	private ArrayList<String> log;
	
	private Process process;
	private Thread thread;
	
	public ConsoleProgram(String name, String relativeJarPath, String arguments) {
		this.name = name;
		this.relativeJarPath = relativeJarPath;
		this.arguments = arguments;
		
		log = new ArrayList<String>();
	}
	
	public JSONObject toJSON() {
		JSONObject encoded = new JSONObject();
		
		encoded.put("name", name);
		encoded.put("path", relativeJarPath);
		encoded.put("args", arguments);
		
		return encoded;
	}
	
	public void start() {
		log.clear();
		running = true;
		
		try{
			ProcessBuilder builder = new ProcessBuilder(("java -jar " + relativeJarPath + " " + arguments).split(" "));
			builder = builder.directory(new File(new File(relativeJarPath).getParent()));
			process = builder.start();
			
			thread = new Thread(() -> {
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
			    while(!Thread.currentThread().isInterrupted()) {
			    	String line = null;
					try {
						line = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    	
			    	if(line != null) {
			    		log.add(line);
			    		if(MainController.getInstance().getCurrentProgram() == this) MainController.getInstance().updateLog();
			    	}
			    }
			});
			
			thread.start();
		}catch(Exception e) { 
			log("Error while starting");
		}
	}
	
	public void shutdown() {
		running = false;
		
		if(process != null) {
			process.destroy();
			process.destroyForcibly();
		}
		
		if(thread != null) {
			thread.interrupt();
		}
		
		process = null;
		thread = null;
	}
	
	public void load(MainController controller) {
		controller.getAreaLog().setText(getFormattedLog());
		
		controller.getBtnDelete().setDisable(false);
		
		if(running) {
			controller.getBtnStart().setDisable(true);
			controller.getBtnStop().setDisable(false);
		}else {
			controller.getBtnStart().setDisable(false);
			controller.getBtnStop().setDisable(true);
		}
		
		controller.getFieldArguments().setEditable(true);
		controller.getFieldArguments().setText(arguments);
	}
	
	public void log(String text) {
		log.add(log.size(), text);
	}
	
	public String getFormattedLog() {
		StringBuilder builder = new StringBuilder();
		
		int i = 0;
		for(String entry : log) {
			if(i == log.size() - 1) {
				builder.append(entry);
			}else {
				builder.append(entry + "\n");
			}
			i++;
		}
		
		return builder.toString();
	}
	
	public ArrayList<String> getLog() {
		return log;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRelativeJarPath() {
		return relativeJarPath;
	}
	
	public String getArguments() {
		return arguments;
	}
	
	public Process getProcess() {
		return process;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public static class Builder {
		private String name, jarPath, arguments;
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setJarPath(String jarPath) {
			this.jarPath = jarPath;
			return this;
		}
		
		public Builder setArguments(String arguments) {
			this.arguments = arguments;
			return this;
		}
		
		public ConsoleProgram build() {
			return new ConsoleProgram(name, jarPath, arguments == null ? "" : arguments);
		}
	}
	
}
