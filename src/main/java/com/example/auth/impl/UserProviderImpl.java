package com.example.auth.impl;

import com.example.auth.User;
import com.example.auth.UserProvider;
import com.example.dao.CustomerDao;
import com.example.entity.Customer;

public class UserProviderImpl implements UserProvider {
	private CustomerDao customerDao;

	public UserProviderImpl(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	@Override
	public User findByApiKey(String apiKey) {
		Customer customer = customerDao.findByApiKey(apiKey);
		if (customer == null) {
			return null;
		}

		User user = new User(customer.getApiKey());
		user.setUsername(customer.getEmail());
		user.setFullName(customer.getDisplayName());
		return user;
	}
}
