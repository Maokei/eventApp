package com.event.business.util;

import java.util.Vector;

public class EventInfo {
	private String title;
	private String city;
	private String startTime, endTime;
	private String content;
	private Vector<String> organizers;
	private Vector<CommentInfo> comments;
	
	public EventInfo() {
		organizers = new Vector<>();
		comments = new Vector<>();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Vector<CommentInfo> getComments() {
		return comments;
	}
	public void setComments(Vector<CommentInfo> comments) {
		this.comments = comments;
	}
	
	public void addOrganizer(String org) {
		organizers.add(org);
	}
	
	public Vector<String> getOrganizers() {
		return organizers;
	}
}
