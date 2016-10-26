package com.event.business.session.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.event.business.logging.AppLogger;
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
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RepositoryService {

	@Inject
	EventProvider eventProvider;

	@Inject
	UserProvider userProvider;

	@Inject
	EventCalendarService eventCalendarService;

	@Resource
	EJBContext context;

	@Inject
	AppLogger logger;

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

		Set<Event> events = new HashSet<>();
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
			if(event.getCity().equalsIgnoreCase("Moskva")) {
				createNewEvent(event);
			} else {
				eventProvider.create(event);
			}
			

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
		return sortByDate(eventProvider.findAll());
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
		try {
			eventProvider.create(event);
			if (!eventCalendarService.createEvent(event)) {
				logger.log("Rolling back createNewEvent for " + event);
				context.setRollbackOnly();
			}
		} catch (EJBException e) {
			logger.log("Caught EJBException in createNewEvent, Rolling back");
			context.setRollbackOnly();
			e.printStackTrace();
		} catch (Exception e) {
			logger.log("Caught Exception in createNewEvent, Rolling back");
			context.setRollbackOnly();
			e.printStackTrace();
		}
	}

	public User findUserByName(String firstName, String lastName) {
		return userProvider.findByName(firstName, lastName);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateUserWithNewEvent(User user, Event event) {
		try {
			User merged = userProvider.update(user);
			merged.setEvent(event);
			event.addUser(merged);
			eventProvider.create(event);
			if (!eventCalendarService.createEvent(event)) {
				logger.log("Rolling back updateUserWithNewEvent for " + event + " for user + " + user);
				context.setRollbackOnly();
			}
		} catch (EJBException e) {
			logger.log("Caught EJBException in updateUserWithNewEvent, Rolling back");
			context.setRollbackOnly();
			e.printStackTrace();
		} catch (Exception e) {
			context.setRollbackOnly();
			logger.log("Caught Exception in updateUserWithNewEvent, Rolling back");
			e.printStackTrace();
		}
	}

	public List<Event> findOverlappingEvents() {
		return eventProvider.findOverlappingEvents();
	}

	public List<String> findUsersHostingFutureEvents() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm");
		String dateStr = df.format(new Date());
		LocalDateTime current_date = LocalDateTime.parse(dateStr, formatter);
		return userProvider.getUsersHostingFutureEvents(current_date);
	}

	public List<Event> getEventsByLocation(String city) {
		return sortByDate(eventProvider.findEventByLocation(city));

	}

	public Event findEventById(Integer id) {
		return eventProvider.findEventById(id);
	}

	public List<User> getUsers() {
		return userProvider.findAll();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Event> sortByDate(List<Event> unsorted) {
		List<Event> sorted = new ArrayList<>();
		unsorted.stream().sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).forEach(sorted::add);
		return sorted;
	}

	public List<User> getOrganizers() {
		return userProvider.getOrganizers();
	}

}
