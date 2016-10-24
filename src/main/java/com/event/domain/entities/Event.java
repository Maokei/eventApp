package com.event.domain.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import com.event.domain.converters.LocalDateTimeConverter;


@NamedQueries({
	  @NamedQuery(name="Event.findAll",
	      query="SELECT e FROM Event e"),
	  @NamedQuery(name="Event.findByPrimaryKey",
	      query="SELECT e FROM Event e WHERE e.id = :id"),
	  @NamedQuery(name="Event.findByTitle",
		  query="SELECT e FROM Event e WHERE LOWER(e.title) = :title"),
	  @NamedQuery(name="Event.findByLocation",
	  query="SELECT e FROM Event e WHERE LOWER(e.city) = :city"),
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
	
	@Convert(converter=LocalDateTimeConverter.class)
	@Column(name = "start_date")
	private LocalDateTime start;
	
	@Convert(converter=LocalDateTimeConverter.class)
	@Column(name = "end_date")
	private LocalDateTime end;
	
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
		this.start = LocalDateTime.parse(start, formatter);
		this.end = LocalDateTime.parse(end, formatter);
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
		this.lastUpdate = LocalDateTime.parse(new Date().toString(), formatter);
	}
	
	public Event(String title, String city, String start, String end, String content) {
		this.title = title;
		this.city = city;
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		this.start = LocalDateTime.parse(start, formatter);
		this.end = LocalDateTime.parse(end, formatter);
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

	public LocalDateTime getStartDate() {
		return start;
	}
	
	public String getStart() {
		formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
		return start.format(formatter).replace("T", " ");
	}

	public void setStart(String startStr) {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		start = LocalDateTime.parse(startStr, formatter);
	}

	public LocalDateTime getEndDate() {
		return end;
	}

	public void setEnd(String endStr) {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		end = LocalDateTime.parse(endStr, formatter);
	}
	
	public String getEnd() {
		formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
		return end.format(formatter).replace("T", " ");
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [title=" + title + ", city=" + city + ", start=" + start + ", end=" + end + ", content=" + content
				+ "]";
	}
	
	
}
