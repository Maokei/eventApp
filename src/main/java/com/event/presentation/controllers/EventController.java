package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.CounterService;
import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.Event;

@Named
@RequestScoped
public class EventController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	RepositoryService repositoryService;
	
	@Inject
	CounterService counterService;
	
	private int count;
	private String query;
	
	private List<Event> events;

	public List<String> completeText(String entry) {
		List<String> results = new ArrayList<String>();
		for (String city : getCityNames()) {
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
	
	private Set<String> getCityNames() {
		List<Event> events = repositoryService.getEvents();
		Set<String> cities = new HashSet<>();
		for(Event event : events) {
			if(!cities.contains(event.getCity()))
				cities.add(event.getCity());
		}
		return cities;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getCount() {
		count = counterService.incrementCounterOnPage("event");
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
