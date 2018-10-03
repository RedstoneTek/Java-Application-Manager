package com.tek.appman.app;

import java.util.ArrayList;
import java.util.Optional;

import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;
import com.tek.appman.fx.controllers.MainController;

public class ProgramManager {
	
	private ArrayList<ConsoleProgram> programs;
	
	public ProgramManager() {
		programs = new ArrayList<ConsoleProgram>();
	}
	
	public void load(JSONObject json) {
		json.getJSONArray("programs").forEach(o -> {
			JSONObject obj = (JSONObject)o;
			
			String name = obj.getString("name");
			String path = obj.getString("path");
			String args = obj.getString("args");
			
			ConsoleProgram program = new ConsoleProgram(name, path, args);
			
			addProgram(program);
		});
	}
	
	public void shutdown() {
		for(ConsoleProgram program : programs) {
			program.shutdown();
		}
		
		programs.clear();
	}
	
	public void addProgram(ConsoleProgram program) {
		programs.add(program);
		MainController.getInstance().addProgramButton(program);
	}
	
	public Optional<ConsoleProgram> getProgramByButton(JFXButton button){
		return programs.stream().filter(cp -> cp.getName().equals(button.getText())).findFirst();
	}
	
	public Optional<ConsoleProgram> getProgramByName(String name){
		return programs.stream().filter(cp -> cp.getName().equals(name)).findFirst();
	}
	
	public ArrayList<ConsoleProgram> getPrograms() {
		return programs;
	}
	
}
