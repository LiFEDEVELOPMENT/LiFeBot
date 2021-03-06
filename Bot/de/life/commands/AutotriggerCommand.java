package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class AutotriggerCommand implements ServerCommand {

	private final String splitter = "\\";

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		switch (args[1].toLowerCase()) {
		case "add":
			addTrigger(m, channel, message);
			break;
		case "delete":
			deleteTrigger(m, channel, message);
			break;
		case "list":
			listTriggers(m, channel, message);
			break;
		case "remove":
			deleteTrigger(m, channel, message);
			break;
		default:
			break;
		}

	}

	private void addTrigger(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (!m.hasPermission(Permission.ADMINISTRATOR)) {
			EmbedMessageBuilder.sendMessage("Trigger hinzufügen",
					"Du hast nicht die Berechtigung, einen Trigger zu erstellen!",
					"Dir fehlt: Permission.ADMINISTRATOR", Color.RED, channel, 10);
			return;
		}

		String[] temp = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).split(Pattern.quote(splitter));
		String answer = String.join(" ", Arrays.copyOfRange(temp, 1, temp.length));
		String trigger = temp[0];

		if (temp.length < 2 || args.length <= 2) {
			EmbedMessageBuilder.sendMessage("Trigger hinzufügen", "Du musst einen Trigger angeben!", Color.RED, channel,
					10);
			return;
		}
		
		trigger = trigger.replaceAll("'", "''");

		ResultSet set = SQLite.onQuery("SELECT * FROM autotriggers WHERE guildid = '" + m.getGuild().getIdLong()
				+ "' AND trigger = '" + trigger + "'");

		try {
			if (set.next()) {
				EmbedMessageBuilder.sendMessage("Trigger hinzufügen", "Dieser Trigger exisitiert bereits.", Color.GRAY,
						channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Trigger hinzugefügt", trigger + "\n" + answer, Color.GRAY, channel,
						10);
			}
		} catch (SQLException ex) {
		}
		
		trigger = trigger.replaceAll("'", "''");
		answer = answer.replaceAll("'", "''");

		SQLite.onUpdate("INSERT INTO autotriggers (guildid,trigger,text) VALUES ('" + m.getGuild().getIdLong() + "','"
				+ trigger + "','" + answer + "')");
		LogMessanger.sendLog(m.getGuild().getIdLong(), "Autotrigger",
				m.getEffectiveName() + " hat den Trigger " + trigger + " mit der Antwort " + answer + " hinzugefügt");
	}

	private void deleteTrigger(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long triggerID = 0l;

		if (args.length <= 2)
			return;

		try {
			triggerID = Long.parseLong(args[2]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Trigger löschen", "Bitte gib eine gültige Trigger-ID an.", Color.GRAY,
					channel, 10);
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM autotriggers WHERE id = '" + triggerID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "'");

		try {
			if (!set.next()) {
				EmbedMessageBuilder.sendMessage("Trigger löschen", "Bitte gib eine gültige Trigger-ID an.", Color.GRAY,
						channel, 10);
				return;
			} else {
				EmbedMessageBuilder.sendMessage("Trigger gelöscht",
						set.getString("trigger") + "\n" + set.getString("text"), Color.GRAY, channel, 10);
				LogMessanger.sendLog(m.getGuild().getIdLong(), "Autotrigger", m.getEffectiveName() + " hat den Trigger "
						+ set.getString("trigger") + " mit der Antwort " + set.getString("text") + " hinzugefügt");
			}
		} catch (SQLException ex) {
		}

		SQLite.onUpdate("DELETE FROM autotriggers WHERE id = '" + triggerID + "' AND guildid = '"
				+ m.getGuild().getIdLong() + "'");
	}

	private void listTriggers(Member m, MessageChannel channel, Message message) {
		String result = "";
		Long guildid = m.getGuild().getIdLong();

		ResultSet set = SQLite.onQuery("SELECT * FROM autotriggers WHERE guildid = '" + guildid + "'");
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ArrayList<String> triggers = new ArrayList<String>();
		ArrayList<String> responses = new ArrayList<String>();

		try {
			while (set.next()) {
				ids.add(set.getInt("id"));
				triggers.add(set.getString("trigger"));
				responses.add(set.getString("text"));
			}
		} catch (SQLException ex) {
		}

		for (int i = 0; i < ids.size(); i++) {
			result = result + triggers.get(i) + "\n" + responses.get(i) + " **(" + ids.get(i) + ")**" + "\n\n";
		}

		if (result == "")
			result = "Auf diesem Server gibt es noch keine Trigger. Füge einen mit !trigger add <trigger\\response> hinzu!";

		EmbedMessageBuilder.sendMessage(result, Color.GRAY, channel);
	}

}
