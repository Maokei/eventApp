package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.Comment;
import com.event.domain.entities.Event;

@Named
@ConversationScoped
public class ViewEventController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	RepositoryService repositoryService;
	
	private Integer id;
	private Event event;
	private List<Comment> comments;

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

	public List<Comment> getComments() {
		event.getComments().stream().sorted(
				(e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
				.forEach(comments::add);
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	

}
