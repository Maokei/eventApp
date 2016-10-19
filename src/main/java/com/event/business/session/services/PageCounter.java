package com.event.business.session.services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name="PageCounter")
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@LocalBean
public class PageCounter {
	
	private Map<String, Integer> counters;
	
    public PageCounter() {
    	counters = new HashMap<>();
    }
    
    @Lock(LockType.WRITE)
    public void incrementCounterOnPage(String page) {
    	if(!counters.containsKey(page)) {
    		counters.put(page, new Integer(1));
    	} else {
    		counters.put(page, counters.get(page) + 1);
    	}
    }
    
    @Lock(LockType.READ)
    public int getPageCountFor(String page) {
    	return counters.get(page);
    }
    
    private void reset() {
    	for(String page : counters.keySet()) {
    		counters.put(page, 0);
    	}
    }
    
    @PostConstruct
    public void startup() {
    	reset();
    }
    
    @PreDestroy
    public void shutdown() {}
}
