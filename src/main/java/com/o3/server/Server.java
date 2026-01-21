package com.o3.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

public class Server {

	public static void main(String[] args) throws Exception {
		try {
			HttpsServer server = HttpsServer.create(new InetSocketAddress(8001), 0);
			SSLContext sslContext = myServerSSLContext();

			server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params) {
					InetSocketAddress remote = params.getClientAddress();
					SSLContext c = getSSLContext();
					SSLParameters sslparams = c.getDefaultSSLParameters();
					params.setSSLParameters(sslparams);
				}
			});

			server.createContext("/datarecord", new MyHandler());
			server.setExecutor(null);
			server.start();
		} catch (IllegalStateException e) {
			System.out.println("Server already started!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid path / context already exists!");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Error with SSL config / server context!");
			e.printStackTrace();
		} catch (BindException e) {
			System.out.println("Cannot bind to the port!");
			e.printStackTrace();
		} catch (FileNotFoundException e){
			System.out.println("Certificate not found!");
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private static SSLContext myServerSSLContext() throws Exception {
		char[] passphrase = "hasl0t0mig0tkA".toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("keystore.jks"), passphrase);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		SSLContext ssl = SSLContext.getInstance("TLS");
		ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return ssl;
	}
}
