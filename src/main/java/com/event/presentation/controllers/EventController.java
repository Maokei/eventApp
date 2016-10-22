package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	private String query;
	private List<Event> events;
	private List<String> cities = 
			Arrays.asList(new String[] { "Moskva", "�stersund", "Hamburg" });

	public List<String> completeText(String entry) {
		List<String> results = new ArrayList<String>();
		for (String city : cities) {
			if (city.toLowerCase().startsWith(entry.toLowerCase())) {
				results.add(city);
			}
		}
		return results;
	}

	public List<Event> getEvents() {
		events = ((query == null) || query.isEmpty()) ? 
				repositoryService.getEvents() :
			repositoryService.getEventsByLocation(query);
		return events;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
}
