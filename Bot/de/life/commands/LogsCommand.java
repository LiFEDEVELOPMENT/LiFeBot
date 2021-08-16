package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.life.LiFeBot;
import de.life.classes.BotError;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LogsCommand implements ServerCommand {

	final EventWaiter waiter;
	final JDA jda = LiFeBot.INSTANCE.getJDA();

	public LogsCommand() {
		this.waiter = LiFeBot.INSTANCE.getWaiter();
	}

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		message.reply(
				"Um einen Log-Channel festzulegen oder den bisherigen Channel zu �ndern, schreibe bitte \"set\".\n"
						+ "Um den bestehenden Log-Channel zu entfernen, schreibe bitte \"delete\".\n"
						+ "Um den Log-Channel dieses Servers anzuzeigen, schreibe bitte \"display\".")
				.queue();

		waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getAuthor().equals(m.getUser())
				&& e.getChannel().equals(channel) && !e.getMessage().equals(message), e -> {
					if (e.getMessage().getContentDisplay().startsWith("set")
							|| e.getMessage().getContentDisplay().startsWith("delete")) {
						message.reply("Bitte gib die ChannelID des Channel an.").queue();

						waiter.waitForEvent(MessageReceivedEvent.class,
								ev -> ev.getAuthor().equals(m.getUser()) && ev.getChannel().equals(channel)
										&& !ev.getMessage().equals(message) && !ev.getMessage().equals(e.getMessage()),
								ev -> {
									if (e.getMessage().getContentDisplay().startsWith("set")) {
										setLogsChannel(m, channel, ev.getMessage());
									}
									if (e.getMessage().getContentDisplay().startsWith("delete")) {
										removeLogsChannel(m, channel, ev.getMessage());
									}
								}, 1, TimeUnit.MINUTES, () -> {
									message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
									return;
								});
						return;
					}
					if (e.getMessage().getContentDisplay().startsWith("display")) {
						displayLogChannel(m, channel, message);
						return;
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});

	}

	public boolean setLogsChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long channelid = null;

		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_MANAGE_CHANNEL.getError(), Color.RED, channel,
					10);
			return false;
		}

		try {
			channelid = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return false;
		}

		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + m.getGuild().getIdLong() + "' AND type = 'log'");

		try {
			if (set.next()) {
				SQLite.onUpdate("UPDATE channel SET channelid = '" + channelid + "' WHERE guildid = '"
						+ m.getGuild().getIdLong() + "' AND type = 'log'");
			} else {
				SQLite.onUpdate("INSERT INTO channel(guildid, channelid, type) VALUES('" + m.getGuild().getIdLong()
						+ "','" + channelid + "','log')");
			}
		} catch (SQLException ex) {
		}
		EmbedMessageBuilder.sendMessage("Log",
				"Der Log-Channel wurde auf #" + m.getGuild().getTextChannelById(channelid).getName() + " gesetzt",
				channel, 10);
		return true;
	}

	private void removeLogsChannel(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		Long channelid = null;

		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_MANAGE_CHANNEL.getError(), Color.RED, channel,
					10);
			return;
		}

		try {
			channelid = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Error", BotError.SYNTAX.getError(), Color.RED, channel, 10);
			return;
		}

		SQLite.onUpdate("DELETE FROM channel WHERE guildid = '" + m.getGuild().getIdLong() + "' AND channelid = '"
				+ channelid + "' AND type='log'");
		EmbedMessageBuilder.sendMessage("Log", "Der Log-Channel dieses Servers wurde entfernt", channel, 10);
	}

	private void displayLogChannel(Member m, MessageChannel channel, Message message) {
		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_MANAGE_CHANNEL.getError(), Color.RED, channel,
					10);
			return;
		}

		if (retrieveLogChannel(m.getGuild()) != null) {
			EmbedMessageBuilder.sendMessage("Log-Channel",
					"Der Log-Channel dieses Server ist der Channel #" + retrieveLogChannel(m.getGuild()).getName()
							+ " mit der ID " + retrieveLogChannel(m.getGuild()).getId(),
					channel);
			return;
		}
		EmbedMessageBuilder.sendMessage("Log-Channel", "Dieser Server hat noch keinen Newschannel", channel);
	}

	private MessageChannel retrieveLogChannel(Guild guild) {
		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + guild.getIdLong() + "' AND type = 'log'");

		try {
			if (set.next()) {
				if (guild.getTextChannelById(set.getLong("channelid")) != null) {
					return guild.getTextChannelById(set.getLong("channelid"));
				}
			}
		} catch (SQLException ex) {
		}
		return null;
	}

	public void autoGenerate(Member m, MessageChannel channel, Message message) {
		Role everyone = jda.getGuildById(m.getGuild().getId()).getRoles()
				.get((jda.getGuildById(m.getGuild().getId()).getRoles().size()) - 1);

		if (retrieveLogChannel(m.getGuild()) != null)
			return;

		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_MANAGE_CHANNEL.getError(), Color.RED, channel,
					10);
			return;
		}

		Long autochannel = m.getGuild().createTextChannel("lifebot-log").complete().getIdLong();

		m.getGuild().getTextChannelById(autochannel).getManager().putPermissionOverride(everyone, null,
				EnumSet.of(Permission.MESSAGE_WRITE));

		SQLite.onUpdate("INSERT INTO channel(guildid, channelid, type) VALUES('" + m.getGuild().getIdLong() + "','"
				+ autochannel + "','log')");
	}
}