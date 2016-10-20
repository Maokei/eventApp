package com.event.business.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class EventParser {
	private Map<String, String> users = new HashMap<>();
	private ArrayList<String> lines;
	private ArrayList<EventInfo> events = new ArrayList<>();
	
	public EventParser(ArrayList<String> listOfLines) {
		lines = listOfLines;
		init();
	}
	
	private void init() {
		mapUsers();
		readEvents();
	}
	
	private void mapUsers() {
		boolean startReading = false;
		for(String line : lines) {
			if(line.startsWith("Events") || line.equals("")) {
				break;
			}
			if(startReading) {
				String[] user = line.split("\\t");
				users.put(user[0], user[1]);
			}
			if(line.startsWith("Users")) {
				startReading = true;
			}
		}
	}
	
	private void readEvents() {
		boolean startReading = false;
		ArrayList<String> eventLines = new ArrayList<>();
		for(String line : lines) {
			if(startReading) {
				if(!line.equals("") && !(line == null)) {
					eventLines.add(line);
				}else {
					getEventInfoAndComments(eventLines);
					eventLines.clear();
				}
			}
			if(line.startsWith("Events")) {
				startReading = true;
			}
		}
		getEventInfoAndComments(eventLines);
		eventLines.clear();
	}
	
	private void getEventInfoAndComments(ArrayList<String> eventLines) {
		EventInfo eventInfo = new EventInfo();
		Vector<CommentInfo> comments = new Vector<>();
		for(int i = 0; i < eventLines.size(); i++) {
			String line = eventLines.get(i);
			if(i == 0) {
				eventInfo = getEvent(line);
			} else {
				CommentInfo comment = new CommentInfo();
				comment.resolveComment(line);
				comments.addElement(comment);
			}
		}
		eventInfo.setComments(comments);
		events.add(eventInfo);
	}

	private EventInfo getEvent(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",[]");
		
		EventInfo eventInfo = new EventInfo();
		eventInfo.setTitle(tokenizer.nextToken().trim());
		eventInfo.setCity(tokenizer.nextToken().trim());
		eventInfo.setContent(tokenizer.nextToken().trim());
		String[] startAndEndTime = tokenizer.nextToken().split("-");
		eventInfo.setStartTime(startAndEndTime[0].trim());
		eventInfo.setEndTime(startAndEndTime[1].trim());
		
		tokenizer.nextToken(); // empty line
		
		int count = tokenizer.countTokens();
		for(int i = 0; i < count; i++) {
			eventInfo.addOrganizer(tokenizer.nextToken().trim());
		}
		return eventInfo;
	}

	public Map<String, String> getUsers() {
		return users;
	}
	
	public String getUserNameByEmail(String email) {
		for(String user : users.keySet()) {
			if(users.get(user).equals(email)) {
				return user;
			}
		}
		return "";
	}
	
	public ArrayList<EventInfo> getEvents() {
		return events;
	}
}