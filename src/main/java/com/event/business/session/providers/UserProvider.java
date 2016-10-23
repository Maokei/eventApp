package com.event.business.session.providers;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import com.event.domain.EntityFacade;
import com.event.domain.entities.User;

public class UserProvider extends EntityFacade<User>{

	@PersistenceContext(unitName = "eventsPU")
	private EntityManager em;
	
	public UserProvider() {
		super(User.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public boolean entityExists(User user) {
		long count = 0;
		try {
			count =  em.createNamedQuery("User.checkIfExists", Long.class)
					.setParameter("firstName", user.getFirstName().toLowerCase())
					.setParameter("lastName", user.getLastName().toLowerCase())
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + user.getFirstName() + " " + user.getLastName());
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return count > 0;
	}

	@Override
	public User findEntity(User user) {
		try {
			return em.createNamedQuery("User.findByPrimaryKey", User.class)
					.setParameter("id", user.getId())
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + user.getFirstName() + " " + user.getLastName() + " with id: " + user.getId());
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public User findByName(String firstName, String lastName) {
		try {
			return em.createNamedQuery("User.findByName", User.class)
					.setParameter("firstName", firstName.toLowerCase())
					.setParameter("lastName", lastName.toLowerCase())
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No Entry found for " + firstName + " " + lastName);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public User findByEmail(String email) {
		try {
			return em.createNamedQuery("User.findByEmail", User.class)
					.setParameter("user_email", email)
					.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No User found for " + email);
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<User> MoreThanOneComment() {
		try {
			return em.createNamedQuery("User.MoreThanOneComments", User.class)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No Users with more than one comment");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<User> getUsersHostingFutureEvents(LocalDate current_date) {
		try {
			return em.createNamedQuery("User.NamesOfUsersHostingFutureEvents", User.class)
					.setParameter("curr_date", current_date)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No users hosting events in the future");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}
	
	public List<User> findOrganizers(Integer id) {
		try {
			return em.createNamedQuery("User.GetOrganizers", User.class)
					.setParameter("e_id", id)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No organizers found for event");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}

	public List<User> findAll() {
		try {
			return em.createNamedQuery("User.findAll", User.class)
					.getResultList();
		} catch (NoResultException e) {
			System.err.println("No Users found");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return null;
	}

}
