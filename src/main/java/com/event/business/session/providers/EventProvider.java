package com.event.business.session.providers;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import com.event.domain.EntityFacade;
import com.event.domain.entities.Event;

@Stateless
@LocalBean
public class EventProvider extends EntityFacade<Event> {

	@PersistenceContext(unitName = "eventsPU")
	private EntityManager em;
	
	public EventProvider() {
		super(Event.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public boolean entityExists(Event event) {
		long count = 0;
		try {
			count =  em.createNamedQuery("Event.checkIfExists", Long.class)
					.setParameter("title", event.getTitle().toLowerCase())
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + event.getTitle());
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return count > 0;
	}

	@Override
	public Event findEntity(Event event) {
		try {
			return em.createNamedQuery("Event.findByPrimaryKey", Event.class)
					.setParameter("id", event.getId())
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + event.getTitle() + " with id: " + event.getId());
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public Event findByTitle(String title) {
		try {
			return em.createNamedQuery("Event.findByTitle", Event.class)
					.setParameter("title", title)
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + title);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<Event> findAll() {
		try {
			return em.createNamedQuery("Event.findAll", Event.class)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No events found");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<Event> findPastEventsIn(String city, LocalDate date) {
		try {
			return em.createNamedQuery("Event.findPastEventsIn", Event.class)
					.setParameter("city", city)
					.setParameter("end_date", date)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + city);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<Event> findFutureEventsIn(String city, LocalDate current_date) {
		try {
			return em.createNamedQuery("Event.findFutureEventsIn", Event.class)
					.setParameter("city", city)
					.setParameter("end_date", current_date)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + city);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<Event> findEventByLocation(String city) {
		try {
			return em.createNamedQuery("Event.findByLocation", Event.class)
					.setParameter("city", city.toLowerCase())
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + city);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<Event> findOverlappingEvents() {
		try {
			return em.createNamedQuery("Event.findOverlappingEvents", Event.class)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No overlapping events found");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}

}
