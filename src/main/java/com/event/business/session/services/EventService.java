package com.event.business.session.services;
/*
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.event.business.events.CommentInfo;
import com.event.business.events.EventInfo;
import com.event.business.events.EventParser;
import com.event.domain.entities.Comment;
import com.event.domain.entities.Event;
import com.event.domain.entities.HitCounter;
import com.event.domain.entities.User;

@Stateless
@LocalBean
public class EventService implements EventServiceLocal, EventServiceRemote {
	@PersistenceContext(unitName = "events")
	private EntityManager em;

	public EventService() {
	}

	@Override
	public Event persistEvent(final Event event) {
		em.persist(event);
		return event;
	}

	@Override
	public Event updateEvent(final Event event) {
		em.merge(event);
		return event;
	}

	@Override
	public User persistUser(final User user) {
		em.persist(user);
		return user;
	}

	@Override
	public User updateUser(final User user) {
		em.merge(user);
		return user;
	}

	@Override
	public Comment persistComment(final Comment comment) {
		em.persist(comment);
		return comment;
	}

	@Override
	public Comment updateComment(final Comment comment) {
		em.merge(comment);
		return comment;
	}

	@Override
	public List<User> findAllUsers() {
		return em.createNamedQuery("User.findAll", User.class).getResultList();
	}

	@Override
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
				em.persist(comment);
			}
		}

		for (final Event event : events) {
			em.persist(event);
			/*
			 * try {
			 * calService.addEventToCalendar(event, new ArrayList<User>(event.getUsers()), new
			 * ArrayList<User>(event.getUsers()));
			 * } catch (IOException e) {
			 * 
			 * e.printStackTrace();
			 * }
			 */
			// addEventToCalendar
			// event.
			/*
			 * try {
			 * System.out.println("Adding to google calendar!!!");
			 * //Context ctx = new InitialContext();
			 * //AnotherBeanLocal a = (AnotherBeanLocal) ctx.lookup("java:comp/env/cal");
			 * //a.doAnother();
			 * 
			 * Context ctx = new InitialContext();
			 * //CalendarLocalBean cs = (CalendarLocalBean) ctx.lookup("java:comp/env/cal");
			 * //cs.addEventToCalendar(event, new ArrayList<User>(event.getUsers()), new
			 * ArrayList<User>(event.getUsers()));
			 * //System.out.println("WE HAVE ADDED SHIT TO CALENDAR RAWWSRRASDASDASD.--------");
			 * 
			 * //Object obj = ctx.lookup("CalendarLocalBean");
			 * //CalendarService bean = (CalendarService) PortableRemoteObject.narrow(obj, CalendarService.class);
			 * //bean.addEventToCalendar(event, new ArrayList<User>(event.getUsers()), new
			 * ArrayList<User>(event.getUsers()));
			 * //cs.printAllCalendarEntries();
			 * } catch (IOException e) {
			 * //System.out.println("IOEXCEPTION");
			 * e.printStackTrace();
			 * }catch(NamingException e) {
			 * System.out.println("NAMING EXCEPTION");
			 * }
			

		}

		for (final User user : users) {
			if (user.getEvents().isEmpty()) {
				em.persist(user);
			}
		}
	}

	@Override
	public User findUserByName(final String firstName, final String lastName) {
		return em.createNamedQuery("User.findByName", User.class).setParameter("firstName", firstName)
				.setParameter("lastName", lastName).getSingleResult();
	}

	@Override
	public User findUserById(final int user_id) {
		return em.createNamedQuery("User.findById", User.class).setParameter("user_id", user_id).getSingleResult();
	}

	public User findUserByEmail(final int user_email) {
		return em.createNamedQuery("User.findByEmail", User.class).setParameter("user_email", user_email)
				.getSingleResult();
	}

	@Override
	public List<User> findOrganizers(final int id) {
		return em.createNamedQuery("User.GetOrganizers", User.class).setParameter("e_id", id).getResultList();
	}

	@Override
	public User findOrganizerById(final int id) {
		return em.createNamedQuery("User.GetOrganizers", User.class).setParameter("e_id", id).getSingleResult();
	}

	@Override
	public long findUsersWithMoreThanThreeComments() {
		return em.createNamedQuery("User.MoreThanOneComments", Long.class).getSingleResult();
	}

	@Override
	public List<String> findFullNamesOfUsersHostingFutureEvents() {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
		final DateFormat df = new SimpleDateFormat("yy/MM/dd");
		final String dateStr = df.format(new Date());
		final LocalDate curr_date = LocalDate.parse(dateStr, formatter);
		return em.createNamedQuery("User.NamesOfUsersHostingFutureEvents", String.class)
				.setParameter("curr_date", curr_date)
				.getResultList();
	}

	@Override
	public void persistEventWithUser(final Event event, final User user) {
		final User detached = em.merge(user);
		detached.setEvent(event);
		event.addUser(detached);
		em.persist(event);
		// persist event google calendar api

	}

	@Override
	public List<Event> findAllEvents() {
		return em.createNamedQuery("Event.findAll", Event.class).getResultList();
	}

	@Override
	public List<Event> findPastEventsInHamburg() {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
		final DateFormat df = new SimpleDateFormat("yy/MM/dd");
		final String dateStr = df.format(new Date());
		final LocalDate end_date = LocalDate.parse(dateStr, formatter);
		return em.createNamedQuery("Event.findPastEventsInHamburg", Event.class).setParameter("city", "Hamburg")
				.setParameter("end_date", end_date).getResultList();
	}

	@Override
	public List<Event> findFutureEventsAt(final String location) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
		final DateFormat df = new SimpleDateFormat("yy/MM/dd");
		final String dateStr = df.format(new Date());
		final LocalDate current_date = LocalDate.parse(dateStr, formatter);
		return em.createNamedQuery("Event.findFutureEventsAt", Event.class).setParameter("city", location.toLowerCase())
				.setParameter("current_date", current_date).getResultList();
	}

	@Override
	public List<Event> findOverlappingEvents() {
		return em.createNamedQuery("Event.findOverlappingEvents", Event.class).getResultList();
	}

	@Override
	public List<HitCounter> findAllCounters() {
		return em.createNamedQuery("HitCounter.getAllCounters", HitCounter.class).getResultList();
	}

	@Override
	public HitCounter getHitCounter(final String page) {
		return em.createNamedQuery("HitCounter.getCounterByPage", HitCounter.class)
				.setParameter("page", page)
				.getSingleResult();
	}

	@Override
	public int getHitCounterHits(final String page) {
		HitCounter hc = null;
		int result = 0;
		hc = em.createNamedQuery("HitCounter.getCounterByPage", HitCounter.class)
				.setParameter("page", page)
				.getSingleResult();
		if (hc != null)
			result = hc.getCounter();
		return result;
	}

	@Override
	public void updateHitCounter(final String page, final int counter) {
		HitCounter hc = null;
		hc = em.createNamedQuery("HitCounter.getCounterByPage", HitCounter.class)
				.setParameter("page", page)
				.getSingleResult();
		hc.setCounter(counter);
		em.merge(hc);
	}

	@Override
	public void persistHitCounterWithNewValue(final HitCounter counter, final int value) {
		final HitCounter detached = em.merge(counter);
		detached.setCounter(value);
		em.persist(detached);
	}

	@Override
	public HitCounter persistHitCounter(final HitCounter counter) {
		em.persist(counter);
		return counter;
	}

}
*/