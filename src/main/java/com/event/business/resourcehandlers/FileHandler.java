package com.event.business.resourcehandlers;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileHandler {
	private Path path;
	private File file;
	
	public FileHandler(){}
	public FileHandler(String fileName){
		path = Paths.get(fileName);
		file = path.toFile();
	}
	
	public void setPath(String strPath) {
		path = Paths.get(strPath);
		file = path.toFile();
	}
	
	public String getPathAsString() {
		return path.toString();
	}
	
	public String getAbsolutePathAsString() {
		return path.toAbsolutePath().toString();
	}
	
	public Path getPath() {
		return path;
	}
	
	public Path getAbsolutePath() {
		return path.toAbsolutePath();
	}
	
	public File getFile() {
		return file;
	}
	
	public void copy(String pathToSrc, String pathToTarget) {
		Path src = Paths.get(pathToSrc);
		Path target = Paths.get(pathToTarget);
		try {
			Files.copy(src, target, REPLACE_EXISTING);
		} catch (IOException e) {
			System.err.println("Failure to copy file");
			e.printStackTrace();
		}
	}
	
	public void move(String pathToSrc, String pathToTarget) {
		Path src = Paths.get(pathToSrc);
		Path target = Paths.get(pathToTarget);
		try {
			Files.move(src, target, REPLACE_EXISTING, COPY_ATTRIBUTES);
		} catch (IOException e) {
			System.err.println("Failure to copy file");
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getListOfLinesFromLocalFile() {
		ArrayList<String> lines = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(getPathAsString()), "UTF8"))) {
			String line;
			
			while((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.err.println("Failure:\n[1] can't open file.\n[2]No permission to read file");
			e.printStackTrace();
		}
		return lines;
	}
	
	public ArrayList<String> getListOfLinesFromURL(String pathToURL) {
		URL url = null;
		ArrayList<String> lines = new ArrayList<>();
		try {
			url = new URL(pathToURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			String line;
		    while((line = in.readLine()) != null) {
		    	lines.add(line);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
}