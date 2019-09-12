package com.nuodb.samples;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class containing {@code main()} to allow this project to be used as a single
 * executable Jar.
 * 
 * @author Paul Chapman
 */
@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		// Ignore --spring.output.ansi.enabled=always flag
		if (args.length > 0 && args[0].startsWith("--"))
			args = Arrays.copyOfRange(args, 1, args.length);

		if (args.length != 3) {
			System.out.println("Expected arguments: <username> <password> <database>");
			System.out.println("   where <database> is either the name of a database on localhost");
			System.out.println("                           or of the form <host>/<db-name>'");
			System.exit(0);
		}

		// Set connection properties from command-line?
		// See method Javadoc for explanation
		DemoBaseClass.setBootPropertiesFromCommandLine(args);

		// ConfigurableApplicationContext ctx = //
		SpringApplication.run(SpringBootJpaDemo.class, args);
		// ctx.close(); // Allow application to terminate

	}
}
