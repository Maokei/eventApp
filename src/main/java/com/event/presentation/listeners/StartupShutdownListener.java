package com.event.presentation.listeners;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.event.business.session.services.FileService;

/**
 * Application Lifecycle Listener implementation class Startup
 *
 */
@WebListener
public class StartupShutdownListener implements ServletContextListener {
	
	@Inject
	FileService fileService;
	
	public StartupShutdownListener() {}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {}
	
	@Override
	public void contextInitialized(final ServletContextEvent e) {
		fileService.createModelObjectsFromFile(e);;
	}
}
