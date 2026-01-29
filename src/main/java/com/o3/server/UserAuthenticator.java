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
		for (User user : userArrayList) {
			if(user.getUsername().equals(username)){
				return false;
			}
		}

		userArrayList.add(new User(username, password, email));
		return true;
	}
}
