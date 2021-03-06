package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class HubsCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		switch (args[1].toLowerCase()) {
		case "add":
			addChannel(m, channel, message);
			break;
		case "delete":
			deleteChannel(m, channel, message);
			break;
		case "list":
			listChannel(m, channel, message);
			break;
		case "remove":
			deleteChannel(m, channel, message);
			break;
		default:
			break;
		}
	}

	private void addChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long channelID = 0l;

		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Hub-Channel", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MANAGE_CHANNEL", Color.RED, channel, 10);
			return;
		}
		if (args.length <= 2) {
			EmbedMessageBuilder.sendMessage("Hub-Channel", "Bitte gib eine Channel-ID an", Color.RED, channel, 10);
			return;
		}

		try {
			channelID = Long.parseLong(args[2]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Hub-Channel", "Bitte gib eine korrekte Channel-ID an", Color.RED, channel,
					10);
			return;
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE channelid = '" + channelID + "' AND type = 'hub'");

		try {
			if (set.next()) {
				EmbedMessageBuilder.sendMessage("Hubchannel hinzufügen", "Dieser Hubchannel exisitiert bereits.",
						Color.GRAY, channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Hubchannel hinzugefügt", Long.toString(channelID), Color.GRAY, channel,
						10);
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("INSERT INTO channel (guildid,channelid,type) VALUES ('" + m.getGuild().getIdLong() + "','"
				+ channelID + "','hub')");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Hub",
				m.getEffectiveName() + " hat ein Hub hinzugefügt: " + channelID);
	}

	private void deleteChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long channelID = 0l;

		if (args.length <= 2) {
			EmbedMessageBuilder.sendMessage("Hubchannel löschen", "Bitte gib eine Channel-ID an.", Color.GRAY, channel,
					10);
			return;
		}

		try {
			channelID = Long.parseLong(args[2]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Hubchannel löschen", "Bitte gib eine gültige Channel-ID an.", Color.GRAY,
					channel, 10);
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE channelid = '" + channelID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "' AND type = 'hub'");

		try {
			if (!set.next()) {
				EmbedMessageBuilder.sendMessage("Hubchannel löschen", "Bitte gib eine gültige Channel-ID an.",
						Color.GRAY, channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Hubchannel gelöscht", Long.toString(set.getLong("channelid")),
						Color.GRAY, channel, 10);
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("DELETE FROM channel WHERE channelid = '" + channelID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "' AND type = 'hub'");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat Channel",
				m.getEffectiveName() + " hat das Hub mit folgender ID gelöscht: " + channelID);
	}

	private void listChannel(Member m, MessageChannel channel, Message message) {
		String result = "";
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'hub'");
		ArrayList<Long> logs = new ArrayList<Long>();

		try {
			while (set.next()) {
				logs.add(set.getLong("channelid"));
			}
		} catch (SQLException ex) {
		}

		for (Long log : logs) {
			result = result + Long.toString(log) + "\n\n";
		}

		if (result == "")
			result = "Auf diesem Server gibt es noch keine Hubs. Füge eins mit !addhubchannel <Channel-ID> hinzu!";

		EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
	}
}