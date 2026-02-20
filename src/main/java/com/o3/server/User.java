package com.o3.server;

public class User {
	private final String username;
	private final String password;
	private String email = "";
	private String nickname = "";

	public User(String username, String password, String email, String nickname) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.nickname = nickname;
	}

	public User(String username, String password){
		this.username = username;
		this.password = password;
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

	public String getNickname() { return nickname; }
}
