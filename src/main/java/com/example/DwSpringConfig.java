package com.example;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.glassfish.jersey.filter.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.auth.ApiKeyAuthenticator;
import com.example.auth.DwAuthFactory;
import com.example.auth.DwAuthenticator;
import com.example.auth.JwtAuthenticator;
import com.example.auth.UserProvider;
import com.example.auth.impl.UserProviderImpl;
import com.example.conf.DwConfiguration;
import com.example.dao.HealthDao;
import com.example.dao.CustomerDao;
import com.example.entity.Customer;
import com.example.health.DbHealthCheck;
import com.example.health.SimpleHealthCheck;
import com.example.rest.DwExceptionMapper;
import com.example.rest.EchoResource;
import com.example.rest.UpperEchoResource;
import com.example.rest.UserResource;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class DwSpringConfig {
	private static final String DS_NAME = "db";

	@Autowired
	private DwConfiguration conf;

	@Autowired
	private Environment env;

	@Autowired
	private CustomerDao customerDao;

	public static void run(DwConfiguration conf, Environment env) {
		GenericApplicationContext parentContext = new GenericApplicationContext();
		parentContext.refresh();
		parentContext.getBeanFactory().registerSingleton("conf", conf);
		parentContext.getBeanFactory().registerSingleton("env", env);

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.getEnvironment(); // without this, access to conf/env will throw NPE
		ctx.setParent(parentContext);
		ctx.register(DwSpringConfig.class);
		ctx.refresh();

		ctx.registerShutdownHook();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void configEnv() {
		env.jersey().register(DwExceptionMapper.class);
		env.jersey().register(LoggingFilter.class);
		env.jersey().register(AuthFactory.binder(dwAuthFactory()));

		env.jersey().register(userResource());
		env.jersey().register(echoResource());
		env.jersey().register(upperEchoResource());

		env.healthChecks().register("simple", simpleHealthCheck());
		env.healthChecks().register(DS_NAME, dbHealthCheck());

		addTestCustomer();
	}

	private void addTestCustomer() {
		Customer customer = new Customer();
		customer.setApiKey("c001");
		customer.setDisplayName("Power User");
		customer.setEmail("customer001@example.com");
		customerDao.save(customer);
	}

	@Bean
	public DwAuthFactory dwAuthFactory() {
		return new DwAuthFactory(dwAuthenticator());
	}

	@Bean
	public DwAuthenticator dwAuthenticator() {
		return new DwAuthenticator(jwtAuthenticator(), apiKeyAuthenticator());
	}

	@Bean
	public JwtAuthenticator jwtAuthenticator() {
		return new JwtAuthenticator(conf.getAuth().getSecret(), conf.getAuth().getExpireMins());
	}

	@Bean
	public ApiKeyAuthenticator apiKeyAuthenticator() {
		return new ApiKeyAuthenticator(userProvider());
	}

	@Bean
	public UserResource userResource() {
		return new UserResource(userProvider(), jwtAuthenticator());
	}

	@Bean
	public UserProvider userProvider() {
		return new UserProviderImpl(customerDao);
	}

	@Bean
	public EchoResource echoResource() {
		return new EchoResource();
	}

	@Bean
	public UpperEchoResource upperEchoResource() {
		return new UpperEchoResource();
	}

	@Bean
	public SimpleHealthCheck simpleHealthCheck() {
		return new SimpleHealthCheck();
	}

	@Bean
	public DbHealthCheck dbHealthCheck() {
		return new DbHealthCheck(env.getHealthCheckExecutorService(),
				conf.getDatabase().getValidationQueryTimeout().or(Duration.seconds(5)),
				healthDao());
	}

	@Bean
	public HealthDao healthDao() {
		return new HealthDao(conf.getDatabase().getValidationQuery());
	}

	@Bean
	public DataSource dataSource() {
		return conf.getDatabase().build(env.metrics(), DS_NAME);
	}

	@Bean
	public AbstractEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setPackagesToScan(Customer.class.getPackage().getName());

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setJpaProperties(additionalProperties());

		return factory;
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", conf.getHbm2ddlAuto());
		return properties;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}
}
