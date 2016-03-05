package com.example.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class HealthDao {
	@PersistenceContext
	private EntityManager entityManager;

	private String validationQuery;

	public HealthDao(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public void check() {
		entityManager.createNativeQuery(validationQuery).getSingleResult();
	}
}
