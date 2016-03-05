package com.example.health;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.example.dao.HealthDao;

import io.dropwizard.db.TimeBoundHealthCheck;
import io.dropwizard.util.Duration;

public class DbHealthCheck extends HealthCheck {
	private static final Logger LOG = LoggerFactory.getLogger(DbHealthCheck.class);

	private final TimeBoundHealthCheck timeBoundHealthCheck;
	private HealthDao healthDao;

	public DbHealthCheck(ExecutorService executorService,
			Duration duration,
			HealthDao healthDao) {
		this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
		this.healthDao = healthDao;
	}

	@Override
	protected Result check() throws Exception {
		return timeBoundHealthCheck.check(() -> {
			healthDao.check();
			return Result.healthy();
		});
	}
}
