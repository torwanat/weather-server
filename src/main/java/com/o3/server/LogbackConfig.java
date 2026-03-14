package com.o3.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

public class LogbackConfig {
	public static void configure() {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.reset();

		String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

		ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
		console.setContext(context);
		console.setName("CONSOLE");

		PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
		consoleEncoder.setContext(context);
		consoleEncoder.setPattern(pattern);
		consoleEncoder.start();
		console.setEncoder(consoleEncoder);
		console.start();

		RollingFileAppender<ILoggingEvent> file = new RollingFileAppender<>();
		file.setContext(context);
		file.setName("FILE");
		file.setFile("logs/server.log");
		file.setAppend(true);

		SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
		policy.setContext(context);
		policy.setParent(file);
		policy.setFileNamePattern("logs/server.%d{yyyy-MM-dd}.%i.log.gz");
		policy.setMaxFileSize(FileSize.valueOf("10MB"));
		policy.setMaxHistory(30);
		policy.setTotalSizeCap(FileSize.valueOf("1GB"));
		policy.start();
		file.setRollingPolicy(policy);

		PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
		fileEncoder.setContext(context);
		fileEncoder.setPattern(pattern);
		fileEncoder.start();
		file.setEncoder(fileEncoder);
		file.start();

		Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		root.addAppender(console);
		root.addAppender(file);
	}
}
