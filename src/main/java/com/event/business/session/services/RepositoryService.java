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

	public void persistEventsFromFile(final EventParser parser, final String contextPath) {
		final Set<String> usernames = parser.getUsers().keySet();
		final ArrayList<User> users = new ArrayList<>();
		for (final String userName : usernames) {
			final String[] fullname = userName.split(" ");
			final User user = new User();
			user.setFirstName(fullname[0]);
			user.setLastName(fullname[1]);
			user.setEmail(parser.getUsers().get(userName));
			final String pictureURL = fullname[0].toLowerCase() + "." + fullname[1].toLowerCase() + ".png";
			user.setPictureURL(pictureURL);
			users.add(user);
		}

		final ArrayList<Event> events = new ArrayList<>();
		for (final EventInfo info : parser.getEvents()) {
			final Event event = new Event();
			event.setTitle(info.getTitle());
			event.setCity(info.getCity());
			event.setStart(info.getStartTime());
			event.setEnd(info.getEndTime());
			event.setContent(info.getContent());

			for (final String organizer : info.getOrganizers()) {
				final String[] userName = parser.getUserNameByEmail(organizer).split(" ");
				for (final User user : users) {
					if (user.getFirstName().equals(userName[0]) && user.getLastName().equals(userName[1])) {
						user.setEvent(event);
						event.addUser(user);
					}
				}
				events.add(event);
			}
		}

		for (final EventInfo info : parser.getEvents()) {
			for (final CommentInfo commentInfo : info.getComments()) {
				final Comment comment = new Comment();
				comment.setComment(commentInfo.getComment());
				comment.setTime(commentInfo.getDate() + " " + commentInfo.getTime());
				final String[] userName = parser.getUserNameByEmail(commentInfo.getMail()).split(" ");
				for (final User user : users) {
					if (user.getFirstName().equals(userName[0]) && user.getLastName().equals(userName[1])) {
						user.setComment(comment);
						comment.setUser(user);
					}
				}
				for (final Event event : events) {
					if (event.getCity().equals(info.getCity()) && event.getTitle().equals(info.getTitle())) {
						comment.setEvent(event);
						event.setComment(comment);
					}
				}
			}
		}

		for (final Event event : events) {
			eventProvider.create(event);
			for (final User user : users) {
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
	
	public void createNewEvent(Event event) {
		eventProvider.create(event);
	}
	
	public User findUserByName(String firstName, String lastName) {
		return userProvider.findByName(firstName, lastName);
	}
	
	public void updateUserWithNewEvent(User user, Event event) {
		// TODO make transactional all or nothing
		
		User merged = userProvider.update(user);
		merged.setEvent(event);
		eventProvider.create(event);
		List<String> organizers = new ArrayList<>();
		organizers.add(new String(user.getFirstName() + " " + user.getLastName()));
		eventCalendarService.createEvent(event, organizers);
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
