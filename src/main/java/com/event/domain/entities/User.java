package com.event.domain.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@NamedQueries({
	  @NamedQuery(name="User.findAll",
			query="SELECT u FROM User u"),
	  @NamedQuery(name="User.findByPrimaryKey",
	  		query="SELECT u FROM User u WHERE u.id = :id"),
	  @NamedQuery(name="User.findByName",
	  		query="SELECT u FROM User u WHERE LOWER(u.firstName) = :firstName"
		  		+ " AND LOWER(u.lastName) = :lastName"),
	  @NamedQuery(name="User.findById",
	  		query="SELECT u FROM User u WHERE u.id = :user_id"),
	  @NamedQuery(name="User.findByEmail",
			query="SELECT u FROM User u WHERE LOWER(u.email) = :user_email"),
	  @NamedQuery(name="User.MoreThanOneComments",
			query="SELECT COUNT(u) FROM User u WHERE (SELECT COUNT(c) FROM Comment c WHERE c.user.id = u.id) > 1"),
	  @NamedQuery(name="User.GetOrganizers",
			query="SELECT u FROM User u JOIN u.events e WHERE e.id = :e_id"),
	  @NamedQuery(name="User.NamesOfUsersHostingFutureEvents",
			query="SELECT DISTINCT CONCAT(u.firstName, ' ', u.lastName) AS name FROM User u JOIN u.events e WHERE e.start > :curr_date"),
	  })

@Entity
@Table(name = "users")
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "firstname")
	private String firstName;
	
	@Column(name = "lastname")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@ManyToMany(mappedBy = "users", fetch=FetchType.EAGER)
	private Set<Event> events = new HashSet<>();
	
	@OneToMany(mappedBy = "user", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Set<Comment> comments = new HashSet<>();
	
	@Column(name="pictureurl")
	private String pictureURL;

	public User() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPictureURL() {
		return pictureURL;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Set<Event> getEvents() {
		return events;
	}

	public void setEvent(Event event) {
		events.add(event);
	}
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComment(Comment comment) {
		comments.add(comment);
	}
	
	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}
}
