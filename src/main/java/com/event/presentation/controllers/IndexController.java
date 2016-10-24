package com.event.presentation.controllers;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.CounterService;

@Named
@ConversationScoped
public class IndexController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	CounterService counterService;
	
	private int count;

	public int getCount() {
		count = counterService.incrementCounterOnPage("index");
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	

}
