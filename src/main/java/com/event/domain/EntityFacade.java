package com.event.domain;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

public abstract class EntityFacade<T> {

	private Class<T> entity;

	public EntityFacade(Class<T> entity) {
		this.entity = entity;
	}

	public boolean isManaged(T entity) {
		return getEntityManager().contains(entity);
	}

	public T create(T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	public T read(Object entityId) {
		return getEntityManager().find(entity, entityId);
	}

	public T update(T entity) {
		return getEntityManager().merge(entity);
	}

	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public void flush() {
		getEntityManager().flush();
	}

	public void clear() {
		getEntityManager().clear();
	}

	public void setFlushMode(FlushModeType type) {
		getEntityManager().setFlushMode(type);
	}

	protected abstract EntityManager getEntityManager();

	public abstract boolean entityExists(T entity);

	public abstract T findEntity(T entity);
}
