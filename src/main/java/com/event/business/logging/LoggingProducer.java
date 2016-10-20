package com.event.business.logging;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class LoggingProducer {

	@Produces
	public AppLogger intercept(InjectionPoint injectionPoint) {
		String nameOfClass = injectionPoint.getMember().getDeclaringClass().getName();
		return Logger.getLogger(nameOfClass)::info;
	}
}
