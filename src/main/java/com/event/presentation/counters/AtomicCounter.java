package com.event.presentation.counters;

import java.util.concurrent.atomic.*;

public class AtomicCounter {
	private final AtomicInteger ticker;
	
	public AtomicCounter(int current) {
		 ticker = new AtomicInteger(current);
	}
	
	public void setCounter(int n) {
		ticker.set(n);
	}

	public int getValue(){
        return ticker.get();
    }
 
    public int getNextValue(){
        return ticker.incrementAndGet();
    }
}
