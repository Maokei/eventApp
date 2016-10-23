package com.event.presentation.controllers;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.Event;

@Named
@ConversationScoped
public class ViewEventController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	RepositoryService repositoryService;
	
	private Integer id;
	private Event event;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Event getEvent() {
		if(event == null) {
			event = repositoryService.findEventById(id);
		}
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
