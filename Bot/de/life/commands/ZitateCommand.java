package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class ZitateCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			randomZitat(m, channel, message);
			return;
		}

		switch (args[1].toLowerCase()) {
		case "add":
			addZitat(m, channel, message);
			break;
		case "delete":
			deleteZitat(m, channel, message);
			break;
		case "list":
			listZitate(m, channel, message);
			break;
		case "remove":
			deleteZitat(m, channel, message);
			break;
		}

		if (args[1].equalsIgnoreCase("Channel")) {
			switch (args[2].toLowerCase()) {
			case "add":
				addChannel(m, channel, message);
				break;
			case "delete":
				deleteChannel(m, channel, message);
				break;
			case "import":
				addExistingChannel(m, channel, message);
				break;
			case "list":
				listChannel(m, channel, message);
				break;
			}

		}

	}

	private void addZitat(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		String[] zitat = Arrays.copyOfRange(args, 2, args.length);

		if (zitat.length < 1) {
			EmbedMessageBuilder.sendMessage("Zitat", "Bitte gib ein Zitat an", Color.RED, channel, 10);

			return;
		}

		String zitatString = "";
		for (String s : zitat) {
			zitatString += s + " ";
		}
		zitatString = zitatString.substring(0, zitatString.length() - 1);
		zitatString = zitatString.replaceAll("'", "''");

		SQLite.onUpdate("INSERT INTO zitate (guildid,zitat,time,author) VALUES ('" + m.getGuild().getIdLong() + "','"
				+ zitatString + "','" + message.getTimeCreated().toString() + "','" + message.getAuthor().getIdLong()
				+ "')");
		EmbedMessageBuilder.sendMessage("Zitat hinzugefügt", zitatString, Color.GREEN, channel, 10);
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat",
				m.getEffectiveName() + " hat ein Zitat hinzugefügt: " + zitatString);
	}

	private void deleteZitat(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (!m.hasPermission(Permission.MESSAGE_MANAGE)) {
			EmbedMessageBuilder.sendMessage("Zitat", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MESSAGE_MANAGE", Color.RED, channel, 10);
			return;
		}

		int id = 0;

		try {
			id = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Zitat löschen", "Bitte gib eine gültige Zitat-ID an.", Color.GRAY, channel,
					10);
		}

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM zitate WHERE id = '" + id + "' AND guildid = '" + m.getGuild().getIdLong() + "'");

		try {
			if (!set.next()) {
				EmbedMessageBuilder.sendMessage("Zitat löschen", "Bitte gib eine gültige Zitat-ID an.", Color.GRAY,
						channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Zitat gelöscht", set.getString("zitat"), Color.GRAY, channel, 10);
				LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat",
						m.getEffectiveName() + " hat ein Zitat gelöscht: " + set.getString("zitat"));
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("DELETE FROM zitate WHERE id = '" + id + "' AND guildid = '" + m.getGuild().getIdLong() + "'");
	}

	private void listZitate(Member m, MessageChannel channel, Message message) {
		Long messageID = channel.sendMessage(getZitatEmbed(1, m.getGuild().getIdLong())).complete().getIdLong();

		if (getZitatePages(m.getGuild().getIdLong()) > 1)
			channel.addReactionById(messageID, "▶").queue();
	}

	private void randomZitat(Member m, MessageChannel channel, Message message) {
		String result = "";
		String resultTime = "";
		Long resultAuthor = 0l;
		OffsetDateTime timeFormatted = null;
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM zitate WHERE guildid = " + guildid);
		ArrayList<String> zitate = new ArrayList<String>();
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<Long> author = new ArrayList<Long>();

		try {
			while (set.next()) {
				zitate.add(set.getString("zitat"));
				time.add(set.getString("time"));
				author.add(set.getLong("author"));
			}
		} catch (SQLException ex) {
		}

		if (zitate.size() >= 1) {
			int zitatNumber = new Random().nextInt(zitate.size());
			result = zitate.get(zitatNumber);
			resultTime = time.get(zitatNumber);
			resultAuthor = author.get(zitatNumber);
			timeFormatted = OffsetDateTime.parse(resultTime);

		}

		if (result == "") {
			result = "Auf diesem Server gibt es noch keine Zitate. Füge eins mit !zitat add <Zitat> hinzu!";
			EmbedMessageBuilder.sendMessage("Zufälliges Zitat", result, Color.YELLOW, channel);
		} else {
			try {
				EmbedMessageBuilder
						.sendMessage("Zufälliges Zitat", result,
								"Erstellt am " + timeFormatted.getDayOfMonth() + "." + timeFormatted.getMonthValue()
										+ "." + timeFormatted.getYear() + " von " + message.getGuild()
												.retrieveMemberById(resultAuthor).complete().getEffectiveName(),
								Color.YELLOW, channel);
			} catch (ErrorResponseException e) {
				EmbedMessageBuilder.sendMessage("Zufälliges Zitat", result,
						"Erstellt am " + timeFormatted.getDayOfMonth() + "." + timeFormatted.getMonthValue() + "."
								+ timeFormatted.getYear() + " von einem nicht mehr existierendem Discord Account.",
						Color.YELLOW, channel);
			}

		}
	}

	private void addExistingChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Integer i = 0;
		Long channelID = 0l;
		ArrayList<Message> reverseList = new ArrayList<Message>();

		if (args.length < 4) {
			EmbedMessageBuilder.sendMessage("Zitat", "Bitte gib eine ChannelID an", Color.RED, channel, 10);
			return;
		}

		if (!m.hasPermission(Permission.MESSAGE_MANAGE)) {
			EmbedMessageBuilder.sendMessage("Zitat", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MESSAGE_MANAGE", Color.RED, channel, 10);
			return;
		}

		try {
			channelID = Long.parseLong(args[3]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Zitat", "Bitte gib eine korrekte ChannelID an", Color.RED, channel, 10);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder().setTitle("Progress").setDescription(
				/* 10x */ ":red_circle::red_circle::red_circle::red_circle::red_circle::red_circle::red_circle::red_circle::red_circle::red_circle:")
				.setColor(Color.RED)
				.setFooter("Bitte warte mit der Ausführung eines Befehles, bis der Bot fertig ist.");

		Long progressMessage = channel.sendMessage(builder.build()).complete().getIdLong();

		MessageChannel zitateChannel = (MessageChannel) m.getGuild().getGuildChannelById(channelID);

		for (Message currMessage : zitateChannel.getIterableHistory().cache(false)) {
			String zitat = currMessage.getContentDisplay();
			zitat = zitat.replaceAll("'", "''");
			reverseList.add(currMessage);
		}

		Collections.reverse(reverseList);

		for (Message currZitat : reverseList) {
			i++;
			if ((i * 10) % reverseList.size() == 0)
				updateProgress((i * 10) / reverseList.size(), message, progressMessage);
			String zitat = currZitat.getContentDisplay();
			zitat = zitat.replaceAll("'", "''");
			if (!zitat.equals("") && (currZitat.getAttachments().size() == 0))
				SQLite.onUpdate("INSERT INTO zitate (guildid,zitat,time,author) VALUES ('" + m.getGuild().getIdLong()
						+ "','" + zitat + "','" + currZitat.getTimeCreated().toString() + "','"
						+ currZitat.getAuthor().getIdLong() + "')");
		}

		builder.setDescription("Fertig");
		builder.setColor(Color.GREEN);
		builder.setFooter("Danke!");
		message.getChannel().editMessageById(progressMessage, builder.build()).complete().delete().queueAfter(10,
				TimeUnit.SECONDS);
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat Channel",
				m.getEffectiveName() + " hat alle Zitate aus dem Channel mit folgender ID hinzugefügt: " + channelID);
	}

	private void addChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long channelID = 0l;

		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Zitat", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MESSAGE_MANAGE", Color.RED, channel, 10);
			return;
		}
		if (args.length < 4) {
			EmbedMessageBuilder.sendMessage("Zitat", "Bitte gib eine ChannelID an", Color.RED, channel, 10);
			return;
		}

		try {
			channelID = Long.parseLong(args[3]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Zitat", "Bitte gib eine korrekte ChannelID an", Color.RED, channel, 10);
			return;
		}

		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE channelid = '" + channelID + "' AND type = 'zitate'");

		try {
			if (set.next()) {
				EmbedMessageBuilder.sendMessage("Zitatechannel hinzufügen", "Dieser Zitatechannel exisitiert bereits.",
						Color.GRAY, channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Zitatechannel hinzugefügt", Long.toString(channelID), Color.GRAY,
						channel, 10);
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("INSERT INTO channel (guildid,channelid,type) VALUES ('" + m.getGuild().getIdLong() + "','"
				+ channelID + "','zitate')");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat Channel",
				m.getEffectiveName() + " hat den Channel mit folgender ID hinzugefügt: " + channelID);
	}

	private void deleteChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 4)
			return;

		Long channelID = 0l;

		try {
			channelID = Long.parseLong(args[3]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Zitatechannel löschen", "Bitte gib eine gültige Channel-ID an.",
					Color.GRAY, channel, 10);
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE channelid = '" + channelID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "' AND type = 'zitate'");

		try {
			if (!set.next()) {
				EmbedMessageBuilder.sendMessage("Zitatechannel löschen", "Bitte gib eine gültige Channel-ID an.",
						Color.GRAY, channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Zitatechannel gelöscht", Long.toString(set.getLong("channelid")),
						Color.GRAY, channel, 10);
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("DELETE FROM channel WHERE channelid = '" + channelID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "' AND type = 'zitate'");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Zitat Channel",
				m.getEffectiveName() + " hat den Channel mit folgender ID gelöscht: " + channelID);
	}

	private void listChannel(Member m, MessageChannel channel, Message message) {
		String result = "";
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM channel WHERE guildid = '" + guildid + "' AND type = 'zitate'");
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
			result = "Auf diesem Server gibt es noch keine Zitatechannel. Füge eins mit !addzitatechannelchannel <Channel-ID> hinzu!";

		EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
	}

	private void updateProgress(int prog, Message message, Long id) {
		EmbedBuilder builder = new EmbedBuilder().setTitle("Progress");
		builder.setColor(Color.RED);
		builder.setFooter("Bitte warte mit der Ausführung eines Befehles, bis der Bot fertig ist.");
		if (prog == 10) {
			builder.setColor(Color.GREEN);
			builder.setFooter("Danke!");
		}
		for (int i = 0; i < 10; i++) {
			if (prog > 0) {
				builder.appendDescription(":green_circle:");
				prog--;
			} else {
				builder.appendDescription(":red_circle:");
			}
		}
		message.getChannel().editMessageById(id, builder.build()).complete();
	}

	public static MessageEmbed getZitatEmbed(int id, Long guildid) {
		ArrayList<String> zitate = new ArrayList<String>();
		ArrayList<Integer> zitateIDs = new ArrayList<Integer>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String foo;
		String result = "";
		Integer i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM zitate WHERE guildid = '" + guildid + "'");

		try {
			while (set.next()) {
				zitate.add(set.getString("zitat"));
				zitateIDs.add(set.getInt("id"));
			}
		} catch (SQLException e) {
		}

		for (int ii = 0; ii < zitate.size(); ii++) {
			if ((foo = result + zitate.get(ii) + " **(" + zitateIDs.get(ii) + ")**\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Zitate").setDescription(result).setFooter("Zitate - Page " + i)
						.setColor(Color.YELLOW).build());
				result = "";
				i++;
			}
			result = result + zitate.get(ii) + " **(" + zitateIDs.get(ii) + ")**\n\n";
		}
		if (result == "")
			result = "Auf diesem Server gibt es noch keine Zitate. Füge eins mit !zitat add <Zitat> hinzu!";
		embeds.add(new EmbedBuilder().setTitle("Zitate").setDescription(result).setFooter("Zitate - Page " + i)
				.setColor(Color.YELLOW).build());

		return embeds.get(id - 1);
	}

	public static Integer getZitatePages(Long guildid) {
		ArrayList<String> zitate = new ArrayList<String>();
		ArrayList<Integer> zitateIDs = new ArrayList<Integer>();
		ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();
		String foo;
		String result = "";
		Integer i = 1;

		ResultSet set = SQLite.onQuery("SELECT * FROM zitate WHERE guildid = '" + guildid + "'");

		try {
			while (set.next()) {
				zitate.add(set.getString("zitat"));
				zitateIDs.add(set.getInt("id"));
			}
		} catch (SQLException e) {
		}

		for (int ii = 0; ii < zitate.size(); ii++) {
			if ((foo = result + zitate.get(ii) + " **(" + zitateIDs.get(ii) + ")**\n\n").length() > 2048) {
				embeds.add(new EmbedBuilder().setTitle("Zitate").setDescription(result).setFooter("Zitate - Page " + i)
						.setColor(Color.CYAN).build());
				result = "";
				i++;
			}
			result = result + zitate.get(ii) + " **(" + zitateIDs.get(ii) + ")**\n\n";
		}
		embeds.add(new EmbedBuilder().setTitle("Zitate").setDescription(result).setFooter("Zitate - Page " + i)
				.setColor(Color.CYAN).build());

		return embeds.size();
	}

}