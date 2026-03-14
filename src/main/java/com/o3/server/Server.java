package com.o3.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.Logger;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

public class Server {

	public static void main(String[] args) {
		LogbackConfig.configure();
		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.info("Starting server");

		try {
			HttpsServer server = HttpsServer.create(new InetSocketAddress(8001), 0);
			SSLContext sslContext = myServerSSLContext(args);

			server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params) {
					SSLContext c = getSSLContext();
					SSLParameters sslparams = c.getDefaultSSLParameters();
					params.setSSLParameters(sslparams);
				}
			});

			final HttpContext serverContext = server.createContext("/datarecord", new ServerHandler());
			UserAuthenticatorDB authenticator = new UserAuthenticatorDB();
			serverContext.setAuthenticator(authenticator);

			final HttpContext collectionContext = server.createContext("/collections", new CollectionHandler());
			collectionContext.setAuthenticator(authenticator);

			server.createContext("/registration", new RegistrationHandler(authenticator));

			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			logger.info("Server started");
		} catch (IllegalStateException e) {
			logger.error("Server already started!", e);
		} catch (IllegalArgumentException e) {
			logger.error("Invalid path / context already exists!", e);
		} catch (NullPointerException e) {
			logger.error("Error with SSL config / server context!", e);
		} catch (BindException e) {
			logger.error("Cannot bind to the port!", e);
		} catch (FileNotFoundException e){
			logger.error("Certificate not found!", e);
		} catch (Exception e){
			logger.error("Unexpected error occurred", e);
		}
	}

	private static SSLContext myServerSSLContext(String[] args) throws Exception {
		char[] passphrase = args[1].toCharArray();
//		char[] passphrase = "passwd".toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(args[0]), passphrase);
//		ks.load(new FileInputStream("keystore.jks"), passphrase);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		SSLContext ssl = SSLContext.getInstance("TLS");
		ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return ssl;
	}
}
