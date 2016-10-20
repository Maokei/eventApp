package com.event.business.session.services;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.event.business.AppConstants;
import com.event.business.resourcehandlers.FileHandler;
import com.event.business.util.EventParser;
import com.event.domain.entities.HitCounter;
import com.event.presentation.counters.AtomicCounter;

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

		final String[] pages = { "error.jsp", "newEvent.jsp", "overview.jsp", "profile.jsp" };
		for (final String page : pages) {
			final HitCounter counter = new HitCounter();
			counter.setPageReferenced(page);
			//repository.persistHitCounter(counter);
		}

		
		// final List<HitCounter> counters = service.findAllCounters();

		final ConcurrentHashMap<String, AtomicCounter> counterMap = new ConcurrentHashMap<>();
		/* for (final HitCounter counter : counters) {
			counterMap.put(counter.getPageReferenced(), new AtomicCounter(counter.getCounter()));
		} */
		servletContext.setAttribute("counterMap", counterMap);
	}
}
