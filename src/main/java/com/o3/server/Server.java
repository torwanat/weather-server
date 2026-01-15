package com.o3.server;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null);
		server.start();
	}
}
