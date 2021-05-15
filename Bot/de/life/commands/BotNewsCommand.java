package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.life.GlobalVariables;
import de.life.LiFeBot;
import de.life.classes.BotError;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotNewsCommand implements ServerCommand {

	final EventWaiter waiter;

	public BotNewsCommand() {
		this.waiter = LiFeBot.INSTANCE.getWaiter();
	}

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		message.reply(
				"Um einen Botnews-Channel festzulegen oder den bisherigen Channel zu ändern, schreibe bitte \"set\".\nUm den bestehenden Botnews-Channel zu entfernen, schreibe bitte \"delete\".\nUm den BotNews-Channel dieses Servers anzuzeigen, schreibe bitte \"display\".")
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
										setNewsChannel(m, channel, ev.getMessage());
									}
									if (e.getMessage().getContentDisplay().startsWith("delete")) {
										removeNewsChannel(m, channel, ev.getMessage());
									}
								}, 1, TimeUnit.MINUTES, () -> {
									message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
									return;
								});
						return;
					}
					if (e.getMessage().getContentDisplay().startsWith("display")) {
						displayMessageChannel(m, channel, message);
						return;
					}
					if (e.getMessage().getContentDisplay().startsWith("news")) {
						message.reply("Bitte schreibe jetzt die News").queue();
						waiter.waitForEvent(MessageReceivedEvent.class,
								evn -> evn.getAuthor().equals(m.getUser()) && evn.getChannel().equals(channel)
										&& !evn.getMessage().equals(message)
										&& !evn.getMessage().equals(e.getMessage()),
								evn -> {
									sendBotNews(m, channel, evn.getMessage());
								}, 1, TimeUnit.MINUTES, () -> {
									message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
									return;
								});
					}
				}, 1, TimeUnit.MINUTES, () -> {
					message.reply("Du hast zu lang gebraucht, probier es nochmal").queue();
					return;
				});

	}

	private void setNewsChannel(Member m, MessageChannel channel, Message message) {
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

		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + m.getGuild().getIdLong() + "' AND type = 'news'");

		try {
			if (set.next()) {
				SQLite.onUpdate("UPDATE channel SET channelid = '" + channelid + "' WHERE guildid = '"
						+ m.getGuild().getIdLong() + "' AND type = 'news'");
			} else {
				SQLite.onUpdate("INSERT INTO channel(guildid, channelid, type) VALUES('" + m.getGuild().getIdLong()
						+ "','" + channelid + "','news')");
			}
		} catch (SQLException ex) {
		}
		EmbedMessageBuilder.sendMessage("News",
				"Der Newschannel wurde auf #" + m.getGuild().getTextChannelById(channelid).getName() + " gesetzt",
				channel, 10);
	}

	private void removeNewsChannel(Member m, MessageChannel channel, Message message) {
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
				+ channelid + "' AND type='news'");
		EmbedMessageBuilder.sendMessage("News", "Der Newschannel dieses Servers wurde entfernt", channel, 10);
	}

	private void displayMessageChannel(Member m, MessageChannel channel, Message message) {
		if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_MANAGE_CHANNEL.getError(), Color.RED, channel,
					10);
			return;
		}

		if (retrieveNewsChannel(m.getGuild()) != null) {
			EmbedMessageBuilder.sendMessage("Newschannel",
					"Der Newschannel dieses Server ist der Channel #" + retrieveNewsChannel(m.getGuild()).getName()
							+ " mit der ID " + retrieveNewsChannel(m.getGuild()).getId(),
					channel);
			return;
		}
		EmbedMessageBuilder.sendMessage("Newschannel", "Dieser Server hat noch keinen Newschannel", channel);
	}

	private void sendBotNews(Member m, MessageChannel channel, Message message) {
		if (m.getUser().getIdLong() != GlobalVariables.userIDLinus
				&& m.getUser().getIdLong() != GlobalVariables.userIDFelix) {
			EmbedMessageBuilder.sendMessage("Error", BotError.PERMISSION_DEV.getError(), Color.RED, channel, 10);
			return;
		}

		for (Guild guild : LiFeBot.INSTANCE.getJDA().getGuilds()) {
			if (retrieveNewsChannel(guild) != null) {
				EmbedMessageBuilder.sendMessage("News", message.getContentDisplay(), m.getUser().getAsTag(),
						retrieveNewsChannel(guild));
			}
		}
	}

	public MessageChannel retrieveNewsChannel(Guild guild) {
		ResultSet set = SQLite
				.onQuery("SELECT * FROM channel WHERE guildid = '" + guild.getIdLong() + "' AND type = 'news'");

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
}