package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.Event;
import com.event.domain.entities.User;

@Named
@ConversationScoped
public class NewEventController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	RepositoryService repositoryService;
	
	
	
	private String title;
	private String city;
	private String organizer;
	private String startDate;
	private String endDate;
	private String content;
	private List<SelectItem> organizers = Arrays.asList(new SelectItem[] {
			new SelectItem("Per Ekeroot", "Per Ekeroot"),
			new SelectItem("Börje Hansson", "Börje Hansson"),
			new SelectItem("Felix Dobslaw", "Felix Dobslaw"),
			new SelectItem("Örjan Sterner", "Örjan Sterner"),
			new SelectItem("Fredrik Aletind", "Fredrik Aletind"),
	});
	
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
	public String getOrganizer() {
		return organizer;
	}
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<SelectItem> getOrganizers() {
		if(organizers == null) {
			initOrganizers();
		}
		return organizers;
	}
	
	public void initOrganizers() {
		List<User> futureHosts = repositoryService.findUsersHostingFutureEvents();
		organizers = new ArrayList<>();
		for(User user : futureHosts) {
			String name = user.getFirstName() + " " + user.getLastName();
			organizers.add(new SelectItem(name, name));
		}
	}
	
	public String addEvent() {
		Event event = new Event();
		event.setTitle(title);
		event.setCity(city);
		event.setStart(startDate);
		event.setEnd(endDate);
		event.setContent(content);
		
		String[] firstAndLastName = organizer.split(" ");
		String firstName = firstAndLastName[0];
		String lastName = firstAndLastName[1];
		
		User user = repositoryService.findUserByName(firstName, lastName);
		repositoryService.updateUserWithNewEvent(user, event);
			
		return "completed";
	}

}
