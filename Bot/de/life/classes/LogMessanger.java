package de.life.classes;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.life.LiFeBot;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LogMessanger {

	final static JDA jda = LiFeBot.INSTANCE.getJDA();

	public static void sendLog(Long guildid, String description) {
		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'log'");

		try {
			while (set.next()) {
				EmbedMessageBuilder.sendMessage(description,
						(MessageChannel) jda.getGuildChannelById(set.getLong("channelid")));
			}
		} catch (SQLException ex) {
		}
	}

	public static void sendLog(Long guildid, String title, String description) {
		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'log'");

		try {
			if (set.next()) {
				EmbedMessageBuilder.sendMessage(title, description,
						(MessageChannel) jda.getGuildChannelById(set.getLong("channelid")));
			}
		} catch (SQLException ex) {
		}
	}

	public static void sendLog(Long guildid, String description, Color color) {
		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'log'");

		try {
			while (set.next()) {
				EmbedMessageBuilder.sendMessage(description, color,
						(MessageChannel) jda.getGuildChannelById(set.getLong("channelid")));
			}
		} catch (SQLException ex) {
		}
	}

	public static void sendLog(Long guildid, String title, String description, Color color) {
		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'log'");

		try {
			while (set.next()) {
				EmbedMessageBuilder.sendMessage(title, description, color,
						(MessageChannel) jda.getGuildChannelById(set.getLong("channelid")));
			}
		} catch (SQLException ex) {
		}
	}
}