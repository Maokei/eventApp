package com.event.presentation.counters;

import java.util.concurrent.atomic.*;

/**
 * @class PageCounter
 * @extends AtomicInteger
 * */
public class PageCounter extends AtomicInteger{
	private static final long serialVersionUID = 1L;
	
	public PageCounter(int current) {
		super(current);
	}
	
	public void setCounter(int n) {
		this.set(n);
	}

	public int getValue(){
        return this.get();
    }
 
    public int getNextValue(){
        return this.incrementAndGet();
    }
}
