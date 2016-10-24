package com.event.presentation.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.event.business.session.services.CounterService;
import com.event.business.session.services.RepositoryService;
import com.event.domain.entities.User;

@Named
@ConversationScoped
public class ProfileController implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	@Inject 
	RepositoryService repositoryService;
	
	@Inject 
	CounterService counterService;

	private int count;
	private List<User> users;

	public List<User> getUsers() {
		if(users == null || users.isEmpty()) {
			users = repositoryService.getUsers();
		}
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getCount() {
		counterService.incrementCounterOnPage("profile");
		count = counterService.getPageCountFor("profile");
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
