package com.event.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	  @NamedQuery(
		name="HitCounter.getCounterByPage",
	    query="SELECT c FROM HitCounter c WHERE c.pageReferenced = :page"),
	  @NamedQuery(
		name="HitCounter.getAllCounters",
		query="SELECT c FROM HitCounter c")
	  })

@Entity
@Table(name = "hitcounters")
public class HitCounter implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name = "counter")
	private int counter;
	
	@Column(name = "page", unique=true)
	private String pageReferenced;
	
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public String getPageReferenced() {
		return pageReferenced;
	}
	public void setPageReferenced(String pageReferenced) {
		this.pageReferenced = pageReferenced;
	}
	
}
