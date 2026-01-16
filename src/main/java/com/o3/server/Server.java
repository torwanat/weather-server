package com.o3.server;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Server {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
		server.createContext("/datarecord", new MyHandler());
		server.setExecutor(null);
		server.start();
	}
}
