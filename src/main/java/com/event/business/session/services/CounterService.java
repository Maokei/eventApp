package com.event.business.session.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
@LocalBean
public class CounterService {
	
	private Map<String, AtomicInteger> counters;
	
	@PostConstruct
    public void initCounterService() {
    	counters = new ConcurrentHashMap<>();
    }
    
    @Lock(LockType.WRITE)
    public int incrementCounterOnPage(String page) {
    	if(!counters.containsKey(page)) {
    		counters.put(page, new AtomicInteger(0));
    	}
    	return counters.get(page).incrementAndGet();
    }
    
    @Lock(LockType.READ)
    public int getPageCountFor(String page) {
    	return counters.get(page).get();
    }
    
    public void reset() {
    	for(String page : counters.keySet()) {
    		counters.put(page, new AtomicInteger(0));
    	}
    }
    
    @PreDestroy
    public void shutdown() {}
}
