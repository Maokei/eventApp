package com.event.business.session.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.event.business.session.providers.EventProvider;
import com.event.business.session.providers.UserProvider;
import com.event.business.util.CommentInfo;
import com.event.business.util.EventInfo;
import com.event.business.util.EventParser;
import com.event.domain.entities.Comment;
import com.event.domain.entities.Event;
import com.event.domain.entities.User;

@Stateless
@LocalBean
public class RepositoryService {

	@Inject
	EventProvider eventProvider;

	@Inject
	UserProvider userProvider;
	
	@Inject
	EventCalendarService eventCalendarService;

	public void persistEventsFromFile(EventParser parser, String contextPath) {
		Set<String> usernames = parser.getUsers().keySet();
		ArrayList<User> users = new ArrayList<>();
		for (String userName : usernames) {
			String[] fullname = userName.split(" ");
			User user = new User();
			user.setFirstName(fullname[0]);
			user.setLastName(fullname[1]);
			user.setEmail(parser.getUsers().get(userName));
			String pictureURL = fullname[0].toLowerCase() + "." + fullname[1].toLowerCase() + ".png";
			user.setPictureURL(pictureURL);
			users.add(user);
		}

		final ArrayList<Event> events = new ArrayList<>();
		for (EventInfo info : parser.getEvents()) {
			Event event = new Event();
			event.setTitle(info.getTitle());
			event.setCity(info.getCity());
			event.setStart(info.getStartTime());
			event.setEnd(info.getEndTime());
			event.setContent(info.getContent());

			for (String organizer : info.getOrganizers()) {
				String[] userName = parser.getUserNameByEmail(organizer).split(" ");
				for (User user : users) {
					if (user.getFirstName().equals(userName[0]) && user.getLastName().equals(userName[1])) {
						user.setEvent(event);
						event.addUser(user);
					}
				}
				events.add(event);
			}
		}

		for (EventInfo info : parser.getEvents()) {
			for (CommentInfo commentInfo : info.getComments()) {
				Comment comment = new Comment();
				comment.setComment(commentInfo.getComment());
				comment.setTime(commentInfo.getDate() + " " + commentInfo.getTime());
				String[] userName = parser.getUserNameByEmail(commentInfo.getMail()).split(" ");
				for (User user : users) {
					if (user.getFirstName().equals(userName[0]) && user.getLastName().equals(userName[1])) {
						user.setComment(comment);
						comment.setUser(user);
					}
				}
				for (Event event : events) {
					if (event.getCity().equals(info.getCity()) && event.getTitle().equals(info.getTitle())) {
						comment.setEvent(event);
						event.setComment(comment);
					}
				}
			}
		}

		for (Event event : events) {
			createNewEvent(event);
			
			for (User user : users) {
				if (user.getEvents().isEmpty()) {
					userProvider.create(user);
				}
			}
		}
	}
	
	public List<User> findUsersWithMoreThanOneComments() {
		return userProvider.MoreThanOneComment();
	}
	
	public List<Event> getEvents() {
		return eventProvider.findAll();
	}
	
	public List<Event> getEventsByLocationAndDate(String location) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
		DateFormat df = new SimpleDateFormat("yy/MM/dd");
		String dateStr = df.format(new Date());
		LocalDate current_date = LocalDate.parse(dateStr, formatter);
		return eventProvider.findFutureEventsIn(location, current_date);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createNewEvent(Event event) {
		eventProvider.create(event);
		eventCalendarService.createEvent(event);
	}
	
	public User findUserByName(String firstName, String lastName) {
		return userProvider.findByName(firstName, lastName);
	}
	
	public void updateUserWithNewEvent(User user, Event event) {
		User merged = userProvider.update(user);
		merged.setEvent(event);
		createNewEvent(event);
	}
	
	public List<Event> findOverlappingEvents() {
		return eventProvider.findOverlappingEvents();
	}
	
	public List<User> findUsersHostingFutureEvents() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
		DateFormat df = new SimpleDateFormat("yy/MM/dd");
		String dateStr = df.format(new Date());
		LocalDate current_date = LocalDate.parse(dateStr, formatter);
		return userProvider.getUsersHostingFutureEvents(current_date);
	}
}
