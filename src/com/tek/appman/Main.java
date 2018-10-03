package com.tek.appman;
	
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.tek.appman.app.ProgramManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static Main instance;
	private ProgramManager programManager;
	
	@Override
	public void start(Stage primaryStage) {
		instance = this;
		
		try {
			programManager = new ProgramManager();
			
			BorderPane root = FXMLLoader.load(Main.class.getResource("/src/com/tek/appman/fx/scenes/Main.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Java Application Manager");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
			primaryStage.setOnCloseRequest(e -> {
				saveStorage();
				programManager.shutdown();
			});
			
			loadStorage();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadStorage() {
		try{
			File storage = new File("storage.json");
		
			if(!storage.exists()) {
				storage.createNewFile();
				
				FileWriter writer = new FileWriter(storage);
				
				writer.write("{}");
				
				writer.close();
			}else {
				String content = "";
				int i = 0;
				
				FileReader reader = new FileReader(storage);
				while((i = reader.read()) != -1) {
					content += (char)i;
				}
				
				reader.close();
				
				try{
					JSONObject json = new JSONObject(content);
					
					programManager.load(json);
				}catch(Exception e) {
					storage.delete();
					storage.createNewFile();
					
					FileWriter writer = new FileWriter(storage);
					
					writer.write("{}");
					
					writer.close();
				}
			}
		}catch(Exception e) { e.printStackTrace(); }
	}
	
	public void saveStorage() {
		JSONObject json = new JSONObject();
		
		json.put("programs", programManager.getPrograms().stream().map(cp -> cp.toJSON()).collect(Collectors.toList()));
	
		try {
			File storage = new File("storage.json");
		
			if(!storage.exists()) {
				storage.createNewFile();
			}
				
			FileWriter writer = new FileWriter(storage);
				
			writer.write(json.toString());
				
			writer.close();
		}catch(Exception e) { e.printStackTrace(); }
		
	}
	
	public ProgramManager getProgramManager() {
		return programManager;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
