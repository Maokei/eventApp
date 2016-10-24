package com.event.business.session.services;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.event.business.AppConstants;
import com.event.business.resourcehandlers.FileHandler;
import com.event.business.util.EventParser;

@Singleton
@Startup
@LocalBean
public class FileService {
	
	@Inject
	RepositoryService repository;
	
	public void createModelObjectsFromFile(final ServletContextEvent e) {
		final FileHandler file = new FileHandler();
		final ArrayList<String> lines = file.getListOfLinesFromURL(AppConstants.events_path);
		final EventParser parser = new EventParser(lines);
		storeEventsInDatabase(parser, e);
	}
	
	private void storeEventsInDatabase(final EventParser parser, final ServletContextEvent e) {
		final ServletContext servletContext = e.getServletContext();
		final String contextRealPath = servletContext.getRealPath("/images/");
		repository.persistEventsFromFile(parser, contextRealPath);
	}
}
