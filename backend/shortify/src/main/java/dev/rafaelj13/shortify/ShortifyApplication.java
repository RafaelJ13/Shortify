package dev.rafaelj13.shortify;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShortifyApplication implements CommandLineRunner {

	@Autowired
	private DatabaseConnection databaseConnection;

	public static void main(String[] args) {
		SpringApplication.run(ShortifyApplication.class, args);
	}

	@Override
	public void run(String... args) throws SQLException {
		Connection conn = databaseConnection.getConnection();
		System.out.println("Database connected: " + (conn != null && !conn.isClosed()));
	}
}
