package com.o3.server;

import com.sun.net.httpserver.BasicAuthenticator;

import java.util.ArrayList;
import java.util.Optional;

public class UserAuthenticator extends BasicAuthenticator {
	private final ArrayList<User> userArrayList;

	public UserAuthenticator () {
		super("datarecord");

		this.userArrayList = new ArrayList<>();
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		return userArrayList.contains(new User(username, password));
	}

	public boolean addUser(String username, String password, String email){
		Optional<User> duplicateUser = userArrayList.stream().findFirst().filter(u -> {
			System.out.println(u.getUsername().equals(username));
			return u.getUsername().equals(username);
		});

		System.out.println(duplicateUser.isPresent());

		if(duplicateUser.isPresent()){
			System.out.println("User present");
			return false;
		}

		System.out.println(userArrayList);

		userArrayList.add(new User(username, password, email));
		return true;
	}
}
