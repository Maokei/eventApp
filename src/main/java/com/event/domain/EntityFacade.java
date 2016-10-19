package com.event.domain;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

public abstract class EntityFacade<T> {
	
	private Class<T> entity;
	
	public EntityFacade(Class<T> entity) {
		this.entity = entity;
	}
	
	public boolean isManaged(T entity) {
		boolean isManaged = false;
		try {
			isManaged = getEntityManager().contains(entity);
		} catch (IllegalArgumentException e) {
			System.err.println("Object " + getSimpleName(entity) + " " + entity + " is not an Entity.");
		}
		return isManaged;
	}
	
	public T create(T entity) {
		try {
			getEntityManager().persist(entity);
		} catch (EntityExistsException e) {
			System.err.println("Entity " + getSimpleName(entity) + " " + entity + " already exists.");
		} catch (TransactionRequiredException e) {
			System.err.println("Transaction required: " + e.getMessage());
		} catch (RollbackException e) {
			System.err.println("Persistence Rollback Exception");
		} catch (OptimisticLockException | PessimisticLockException e) {
			System.err.println("Persistence Lock Exception");
		} catch (PersistenceException e) {
			System.err.println("PersistenceException: " + e.getMessage());
		}
		return entity;
	}
	
	public T read(Object entityId) {
		return getEntityManager().find(entity, entityId);
	}
		
	public T update(T entity) {
		try {
			getEntityManager().merge(entity);
		} catch (IllegalArgumentException e) {
			System.err.println("Merging " + getSimpleName(entity) + " " + entity + "Failed!\nIt is probably removed:" + e.getMessage());
		} catch (TransactionRequiredException e) {
			System.err.println("Merging " + getSimpleName(entity) + " " + entity + "Failed!\nTransaction is required:" + e.getMessage());
		}
		return entity;
	}
	
	public void remove(T entity) {
		try {
			getEntityManager().remove(getEntityManager().merge(entity));
		} catch (IllegalArgumentException e) {
			System.err.println("Removing " + getSimpleName(entity) + " " + entity + "Failed!\n" + e.getMessage());
		} catch (PersistenceException e) {
			System.err.println("Removing " + getSimpleName(entity) + " " + entity + "Failed!\n" + e.getMessage());
		}
		
	}
	
	public void flush() {
		try {
			getEntityManager().flush();
		} catch (TransactionRequiredException e) {
			System.err.println("Flush Failed, No transaction is associated with EntityManager: " + e.getMessage());
		} catch (PersistenceException e) {
			System.err.println("Flush Failed: " + e.getMessage());
		}
	}

	public void clear() {
		getEntityManager().clear();
		System.out.println("Clearing the persistence context. All managed entities are detached");
	}
	
	public void setFlushMode(FlushModeType type) {
		getEntityManager().setFlushMode(type);
	}
	
	private String getSimpleName(T entity) {
		return entity.getClass().getSimpleName();
	}
	
	protected abstract EntityManager getEntityManager();
	
	public abstract boolean entityExists(T entity);
	
	public abstract T findEntity(T entity);
}

