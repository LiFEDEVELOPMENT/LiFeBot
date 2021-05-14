package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import de.life.classes.BotError;
import de.life.classes.EmbedMessageBuilder;
import de.life.classes.Warning;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class WarnCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		switch (args[1]) {
		case "clear":

			break;

		default:
			break;
		}
	}

	public void addWarn(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (!m.hasPermission(Permission.KICK_MEMBERS)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_KICK.getError(), Color.RED, channel, 10);
			return;
		}

		if (message.getMentionedUsers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return;
		}

		String time = message.getTimeCreated().toString();
		Long guildid = m.getGuild().getIdLong();
		Long userid = message.getMentionedUsers().get(0).getIdLong();
		Long moderatorid = m.getUser().getIdLong();
		String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

		SQLite.onUpdate("INSERT INTO warns(guildid, userid, moderatorid, reason, time) VALUES ('" + guildid + "', '"
				+ userid + "', '" + moderatorid + "', '" + reason + "', '" + time + "')");
	}

	public void clearWarns(Member m, MessageChannel channel, Message message) {

		if (!m.hasPermission(Permission.KICK_MEMBERS)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_KICK.getError(), Color.RED, channel, 10);
			return;
		}

		if (message.getMentionedUsers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return;
		}

		SQLite.onUpdate("DELETE FROM warns WHERE guildid = '" + m.getGuild().getIdLong() + "' AND userid = '"
				+ message.getMentionedUsers().get(0).getIdLong() + "'");
	}

	public void listWarnings(Member m, MessageChannel channel, Message message) {
		ArrayList<Warning> warnings = new ArrayList<>();

		if (!m.hasPermission(Permission.KICK_MEMBERS)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_KICK.getError(), Color.RED, channel, 10);
			return;
		}

		if (message.getMentionedUsers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return;
		}

		ResultSet set = SQLite.onQuery("SELECT * FROM warns WHERE guildid = '" + m.getGuild().getIdLong()
				+ "' AND userid = '" + message.getMentionedUsers().get(0).getIdLong() + "'");

		try {
			while (set.next()) {
				String reason = (set.getString("reason") != null) ? set.getString("reason")
						: "Keine Begründung angegeben";

				warnings.add(new Warning(set.getLong("guilduid"), set.getLong("moderatorid"), set.getLong("userid"),
						reason, set.getString("time")));
			}
		} catch (SQLException e) {
		}  
	}

}