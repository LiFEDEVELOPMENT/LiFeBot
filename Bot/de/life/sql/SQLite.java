package de.life.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

	private static Connection conn;
	private static Statement stmt;

	public static void connect() {
		conn = null;

		try {
			File file = new File("LiFedata.db");
			if (!file.exists())
				file.createNewFile();

			String url = "jdbc:sqlite:" + file.getPath();

			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();

			System.out.println("Verbindung zur LiFe-DB bereit.");

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		try {
			if (conn == null)
				return;

			conn.close();

			System.out.println("Verbing zur LiFe-DB unterbrochen.");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void onUpdate(String sql) {
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet onQuery(String sql) {

		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}