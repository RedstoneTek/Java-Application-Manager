package com.tek.appman.fx.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.tek.appman.Main;
import com.tek.appman.app.ConsoleProgram;
import com.tek.appman.app.NewApplicationDialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MainController {

	private static MainController instance;
	
	@FXML
	private JFXButton btnNewProgram;
	
	@FXML
	private JFXButton btnStart;
	
	@FXML
	private JFXButton btnStop;
	
	@FXML
	private JFXButton btnDelete;
	
	@FXML
	private VBox paneProgramList;
	
	@FXML
	private JFXTextArea areaLog;
	
	@FXML
	private JFXTextField fieldArguments;
	
	private ConsoleProgram currentProgram;
	
	@FXML
	private void initialize() {
		instance = this;
		
		areaLog.setEditable(false);
		fieldArguments.setEditable(false);
		
		btnStart.setOnMouseClicked(e -> {
			currentProgram.start();
			
			if(currentProgram.isRunning()) {
				btnStart.setDisable(true);
				btnStop.setDisable(false);
			}
		});
		
		btnStop.setOnMouseClicked(e -> {
			currentProgram.shutdown();
			
			if(!currentProgram.isRunning()) {
				btnStart.setDisable(false);
				btnStop.setDisable(true);
			}
		});
		
		areaLog.textProperty().addListener(e -> {
			areaLog.setScrollTop(Double.MAX_VALUE);
		});
		
		btnNewProgram.setOnMouseClicked(e -> {
			new Thread(() -> NewApplicationDialog.open()).start();
		});
		
		btnDelete.setOnMouseClicked(e -> {
			deleteProgram();
		});
		
		clearSelection();
	}
	
	public void clearSelection() {
		disable(btnStart, btnStop, btnDelete);
		fieldArguments.setText("");
		fieldArguments.setEditable(false);
		areaLog.setText("");
	}
	
	public void updateLog() {
		this.areaLog.setText(currentProgram.getFormattedLog());
	}
	
	public void addProgramButton(ConsoleProgram program) {
		JFXButton button = new JFXButton(program.getName());
		button.setPrefWidth(125);
		button.setPrefHeight(40);
		
		button.setOnMouseClicked(e -> {
			Optional<ConsoleProgram> foundProgram = Main.getInstance().getProgramManager().getProgramByButton(button);
		
			if(foundProgram.isPresent()) {
				foundProgram.get().load(this);
				currentProgram = foundProgram.get();
			}
		});
		
		paneProgramList.getChildren().add(button);
	}
	
	public void deleteProgram() {
		Platform.runLater(() -> {
			currentProgram.shutdown();
			for(Node n : new ArrayList<Node>(paneProgramList.getChildren())) {
				if(n instanceof JFXButton) {
					JFXButton button = (JFXButton)n;
					if(button.getText().equals(currentProgram.getName())) {
						paneProgramList.getChildren().remove(button);
					}
				}
			}
			Main.getInstance().getProgramManager().getPrograms().remove(currentProgram);
			currentProgram = null;
			
			clearSelection();
		});
	}
	
	public JFXButton getBtnNewProgram() {
		return btnNewProgram;
	}

	public JFXButton getBtnStart() {
		return btnStart;
	}

	public JFXButton getBtnStop() {
		return btnStop;
	}

	public VBox getPaneProgramList() {
		return paneProgramList;
	}

	public JFXTextArea getAreaLog() {
		return areaLog;
	}

	public JFXTextField getFieldArguments() {
		return fieldArguments;
	}

	public void disable(JFXButton... nodes) {
		Arrays.asList(nodes).stream().forEach(jfxb -> jfxb.setDisable(true));
	}
	
	public void enable(JFXButton... nodes) {
		Arrays.asList(nodes).stream().forEach(jfxb -> jfxb.setDisable(false));
	}
	
	public ConsoleProgram getCurrentProgram() {
		return currentProgram;
	}
	
	public JFXButton getBtnDelete() {
		return btnDelete;
	}
	
	public static MainController getInstance() {
		return instance;
	}
}
