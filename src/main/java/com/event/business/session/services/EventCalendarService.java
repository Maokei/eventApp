package com.event.business.session.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;

import com.event.business.AppConstants;
import com.event.business.logging.AppLogger;
import com.event.domain.entities.Comment;
import com.event.domain.entities.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Organizer;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

@Singleton
public class EventCalendarService {
	
	@Inject
	AppLogger logger;
	
	private Calendar service;
	private NetHttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	
	@PostConstruct
	public void setupPrimaryCalendarService() {
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
		service = initCalendarService();
		initPrimary();
	}
	
	@PreDestroy
	public void teardownOfCalendars() {
		try {
			logger.log("Clearing all events in primary calendar");
			service.calendars().clear("primary").execute();
		} catch (IOException e) {
			logger.log("Exception while clearing Calendar events");
			e.printStackTrace();
		}
	}
	
	private void initPrimary() {
		try {
			service.calendars().get("primary")
				.execute()
				.setSummary("Event Organizer")
				.setTimeZone("Europe/Stockholm");
		} catch (IOException e) {
			logger.log("Exception while setting summary and timezone");
			e.printStackTrace();
		}
	}
	
	private Calendar initCalendarService() {
		GoogleCredential credential = new GoogleCredential.Builder()
				.setClientSecrets(AppConstants.clientId, AppConstants.clientSecret)
				.setJsonFactory(jsonFactory).setTransport(httpTransport)
				.build().setRefreshToken(AppConstants.refreshToken);

		return new Calendar.Builder(httpTransport, jsonFactory, credential)
				.build();
	}
	
	public void createEvent(com.event.domain.entities.Event event) {
		try {
			List<EventAttendee> attendees = getAttendees(event);
			Event calendarEvent = setupCalendarEvent(event, attendees);
			service.events().insert("primary", calendarEvent).execute();
		} catch (IOException e) {
			logger.log("Exception while creating an CalendarEvent");
			e.printStackTrace();
		}
	}
	
	private List<EventAttendee> getAttendees(com.event.domain.entities.Event event) {
		List<EventAttendee> attendees = new ArrayList<>();
		Set<Comment> comments = event.getComments();
		for(Comment comment : comments) {
			User user = comment.getUser();
			EventAttendee attendee = new EventAttendee();
			attendee.setDisplayName(user.getFirstName() + " " + user.getLastName());
			attendee.setComment(comment.getComment());
			attendee.setEmail(user.getEmail());
			attendees.add(attendee);
		}
		return attendees;
	}
	
	private Event setupCalendarEvent(com.event.domain.entities.Event event, List<EventAttendee> attendees) {
	
		Event calendarEvent = new Event();
		calendarEvent.setAttendees(attendees);
		calendarEvent.setSummary(event.getTitle());
		calendarEvent.setDescription(event.getContent());
		
		String created = LocalDateTime.now().toString();
		calendarEvent.setCreated(new DateTime(created));
		
		EventDateTime edt_start = new EventDateTime();
		edt_start.setDateTime(new DateTime(event.getStartFull()+":00"));
		edt_start.setTimeZone("Europe/Stockholm");
		calendarEvent.setStart(edt_start);
		
		EventDateTime edt_end = new EventDateTime();
		edt_end.setDateTime(new DateTime(event.getEndFull()+":00"));
		edt_end.setTimeZone("Europe/Stockholm");
		calendarEvent.setEnd(edt_end);
		
		calendarEvent.setGuestsCanSeeOtherGuests(true);
		calendarEvent.setLocation(event.getCity());
		Organizer organizer = new Organizer();
		User user = event.getUsers().iterator().next();
		organizer.setDisplayName(user.getFirstName() + " " + user.getLastName());
		organizer.setEmail(user.getEmail());
		calendarEvent.setOrganizer(organizer);
		return calendarEvent;
	}

}
