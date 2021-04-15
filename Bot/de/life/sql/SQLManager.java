package de.life.sql;

public class SQLManager {

	public static void onCreate() {

		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS channel(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, channelid INTEGER, type STRING)");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS zitate(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, zitat STRING, time STRING, author INTEGER)");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS memes(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, meme STRING)");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS polls(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, channelid INTEGER, messageid INTEGER, userid INTEGER, answercount INTEGER)");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS pollvotes(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, pollid INTEGER, userid INTEGER, vote INTEGER)");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS autotriggers(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildid INTEGER, trigger STRING, text STRING)");
		SQLite.onUpdate("DROP TABLE IF EXISTS commands");
		SQLite.onUpdate(
				"CREATE TABLE IF NOT EXISTS commands(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, command STRING)");
		FillCommandsTable.fillTable();
	}
}