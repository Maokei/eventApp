package com.event.domain.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.event.domain.converters.LocalDateConverter;
import com.event.domain.converters.LocalDateTimeConverter;


@NamedQueries({
	  @NamedQuery(name="Event.findAll",
	      query="SELECT e FROM Event e"),
	  @NamedQuery(name="Event.findByPrimaryKey",
	      query="SELECT e FROM Event e WHERE e.id = :id"),
	  @NamedQuery(name="Event.findByTitle",
		  query="SELECT e FROM Event e WHERE LOWER(e.title) = :title"),
	  @NamedQuery(name="Event.findPastEventsInHamburg",
	      query="SELECT e FROM Event e WHERE LOWER(e.city) = :city"
	      		+ " AND e.end < :end_date"),
	  @NamedQuery(name="Event.findFutureEventsAt",
      query="SELECT e FROM Event e WHERE LOWER(e.city) = :city"
      		+ " AND e.start >= :current_date"),
	 @NamedQuery(name="Event.findOverlappingEvents",
		  query="SELECT e1 FROM Event e1, Event e2"
		  		+ " WHERE e1.id <> e2.id"
		  		+ " AND e1.end >= e2.start"
		  		+ " AND e2.end >= e1.start")
	  })
@Entity
@Table(name = "events")
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Transient
	private transient DateTimeFormatter formatter;
	
	@Id
	@Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "city")
	private String city;
	
	@Convert(converter=LocalDateConverter.class)
	@Column(name = "start_date")
	private LocalDate start;
	
	@Convert(converter=LocalDateConverter.class)
	@Column(name = "end_date")
	private LocalDate end;
	
	@Convert(converter=LocalDateTimeConverter.class)
	@Column(name = "lastupdate")
	private LocalDateTime lastUpdate;
	
	@Column(name = "content")
	private String content;
	
	@ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinTable(name = "organizers",
	joinColumns=@JoinColumn(name = "event_id", referencedColumnName = "id"),
	inverseJoinColumns=@JoinColumn(name = "user_id", referencedColumnName = "id"))
	private Set<User> users = new HashSet<>(); 
	
	
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
	private Set<Comment> comments = new HashSet<>(); 
	
	public Event() {}
	
	public Event(int id, String title, String city, String start, String end, String content) {
		this.id = id;
		this.title = title;
		this.city = city;
		this.content = content;
		
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		this.start = LocalDate.parse(start, formatter);
		this.end = LocalDate.parse(end, formatter);
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
		this.lastUpdate = LocalDateTime.parse(new Date().toString(), formatter);
	}
	
	public Event(String title, String city, String start, String end, String content) {
		this.title = title;
		this.city = city;
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		this.start = LocalDate.parse(start, formatter);
		this.end = LocalDate.parse(end, formatter);
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
		this.lastUpdate = LocalDateTime.parse(new Date().toString(), formatter);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(String startStr) {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		start = LocalDate.parse(startStr, formatter);
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(String endStr) {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		end = LocalDate.parse(endStr, formatter);
	}

	public String getLastUpdate() {
		return lastUpdate.toString();
	}

	@PrePersist
	private void setLastUpdate() {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
		lastUpdate = LocalDateTime.parse(getCurrentTimeAsString(), formatter);
	}
	
	private String getCurrentTimeAsString() {
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		return df.format(new Date());
	}
	
	@PreUpdate
	private void updateLastUpdate() {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
		lastUpdate = LocalDateTime.parse(getCurrentTimeAsString(), formatter);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void addUser(User user) {
		users.add(user);
	}
	
	public Set<User> getUsers() {
		return users;
	}
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComment(Comment comment) {
		comments.add(comment);
	}
}
