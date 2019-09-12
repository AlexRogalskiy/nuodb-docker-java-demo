package com.nuodb.samples.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nuodb.samples.ConnectionHandler;

@RestController
public class InfoController {

	protected static final Logger logger = LoggerFactory.getLogger(InfoController.class);

	@GetMapping("/")
	public String home() {
		logger.info("Root URL");
		return "Possible URLs are:" //
				+ "<ul>" //
				+ "<li><a href='/accounts'>List all accounts: /accounts</a>" //
				+ "<li><a href='/accounts/search/m'>Find all accounts whose name contains m: /accounts/search/m</a>"
				+ "<li><a href='/info'>Show connection information: /info</a>" //
				+ "<li><a href='/shutdown'>Shutdown this application: /shutdown</a>" //
				+ "</ul>";
	}

	@GetMapping("/info")
	public ConnectionHandler info() {
		logger.info("info URL");
		return ConnectionHandler.getConnectionInfo();
	}

	@GetMapping("/shutdown")
	public void shutdown() {
		logger.warn("Application shutting down on request");
		System.exit(0);
	}
}
