package com.event.domain.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.event.domain.converters.LocalDateTimeConverter;


@NamedQueries({
	  @NamedQuery(
		name="Comment.MoreThanOneComments",
	    query="SELECT c FROM Comment c HAVING COUNT(c.user) > 1 ORDER BY c.user DESC")
	  })
@Entity
@Table(name = "comments")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Transient
	private transient DateTimeFormatter formatter;
	
	@Id
	@Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;
	
	@Column(name = "comment")
	private String comment;
	
	@Convert(converter=LocalDateTimeConverter.class)
	@Column(name = "time")
	private LocalDateTime time;
	
	@Convert(converter=LocalDateTimeConverter.class)
	@Column(name = "lastupdate")
	private LocalDateTime lastUpdate;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Comment() {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTime() {
		return time.toString()
				.replace('T', ' ');
	}
	
	public LocalDateTime getDateTime() {
		return time;
	}

	public void setTime(String timeStr) {
		formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm");
		time = LocalDateTime.parse(timeStr, formatter);
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
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
