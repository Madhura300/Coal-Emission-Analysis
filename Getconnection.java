package com.coalemission.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Getconnection {
	private static Properties dbProperties = new Properties();

	// load the database URL, username, and password into the properties object
	static {
        try (FileReader reader = new FileReader("src/db.properties")) {
            dbProperties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read db.properties file", e);
        }
    }

	public static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(
			dbProperties.getProperty("db.url"), 
			dbProperties.getProperty("db.username"), 
			dbProperties.getProperty("db.password"));
		
	}

}
