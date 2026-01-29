package com.o3.server;

import java.util.Objects;

public class User {
	private String username;
	private String password;
	private String email;

	public User(String username, String password, String email){
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(String username, String password){
		this.username = username;
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)	return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		User user = (User) obj;
		return Objects.equals(username, user.username) && Objects.equals(password, user.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, password);
	}
}
