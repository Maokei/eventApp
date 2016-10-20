package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.Event;

@Named
@ConversationScoped
public class EventController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	RepositoryService repositoryService;
	
	private Event selected;
	private List<Event> events;
	
	public Event getSelected() {
		return selected;
	}
	public void setSelected(Event selected) {
		this.selected = selected;
	}
	
	public List<Event> getEvents() {
		if(events == null || events.isEmpty()) {
			events = repositoryService.getEvents();
		}
		if(events == null || events.isEmpty()) {
			System.out.println("***** events is null or empty ******");
		}
		return events;
	}
}
