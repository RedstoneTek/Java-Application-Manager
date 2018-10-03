package com.tek.appman.app;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.tek.appman.Main;
import com.tek.appman.app.ConsoleProgram.Builder;

import javafx.application.Platform;

public class NewApplicationDialog {
	
	public static void open() {
		Builder builder = new Builder();
		
		String name = JOptionPane.showInputDialog("What is the program's nickname");
		
		if(name != null && !name.isEmpty() && !Main.getInstance().getProgramManager().getProgramByName(name).isPresent()) {
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Executable Jar File", ".jar"));
			
			int returnCode = chooser.showOpenDialog(null);
			
			if(returnCode == JFileChooser.APPROVE_OPTION) {
				try{
					File file = chooser.getSelectedFile();
					ConsoleProgram program = builder.setJarPath(file.getCanonicalPath()).setName(name).build();
					Platform.runLater(() -> Main.getInstance().getProgramManager().addProgram(program));
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Internal Error");
				}
			}else {
				JOptionPane.showMessageDialog(null, "Error while loading file");
			}
		}else {
			JOptionPane.showMessageDialog(null, "This already exists or the name is empty");
		}
	}
	
}
