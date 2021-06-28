package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.PrivateVoiceManager;
import de.life.interfaces.ServerCommand;
import de.life.listener.VoiceListener;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PrivateChannelCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			createChannel(m, channel, message);
			return;
		}
		switch (args[1].toLowerCase()) {
		case "lockall":
			lockAll(m, channel, message);
			break;
		case "lock":
			lock(m, channel, message);
			break;
		case "unlockall":
			unlockAll(m, channel, message);
			break;
		case "unlock":
			unlock(m, channel, message);
			break;
		case "userlimit":
			userlimit(m, channel, message);
			break;
		default:
			break;
		}
	}

	private void createChannel(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		VoiceChannel hub = null;

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM channel WHERE guildid = '" + message.getGuild().getIdLong() + "' AND type = 'hub'");

		try {
			if (set.next()) {

				hub = m.getGuild().getVoiceChannelById(set.getLong("channelid"));
			} else {
				EmbedMessageBuilder.sendMessage(
						"Es gibt keinen Hub-Channel auf diesem Server. Bitte einen Serveradministrator, einen mit !hub add <Channel-ID> zu erstellen.",
						Color.CYAN, message.getChannel(), 10);
				return;
			}
		} catch (SQLException e) {

		}

		if (!m.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du befindest dich in keinem Voicechannel.").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);

			return;
		}

		if (m.getVoiceState().getChannel().getName().substring(7).equalsIgnoreCase(m.getEffectiveName())) {
			channel.sendMessage("Du befindest dich bereits in einem temporären Voicechannel.").complete().delete()
					.queueAfter(5, TimeUnit.SECONDS);

			return;
		}

		VoiceListener.INSTANCE.onJoin(hub, m);
	}

	private void lockAll(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		PrivateVoiceManager.lockAll(m.getGuild(), m);
		channel.sendMessage("Du hast deinen Channel für jeden gesperrt!").queue();
	}

	private void lock(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", gib bitte mindestens einen User an, den du sperren möchtest!", Color.RED,
					channel, 5);

			return;
		}

		PrivateVoiceManager.lockChannel(m.getGuild(), m, message.getMentionedMembers());
	}

	private void unlockAll(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		PrivateVoiceManager.unlockAll(m.getGuild(), m);
		channel.sendMessage("Du hast deinen Channel für jeden freigegeben!").queue();
	}

	private void unlock(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", gib bitte mindestens einen User an, den du freigeben möchtest!", Color.RED,
					channel, 5);

			return;
		}

		PrivateVoiceManager.unlockChannel(m.getGuild(), m, message.getMentionedMembers());
	}

	private void userlimit(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 3) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", gib bitte eine Nutzeranzahl an!", Color.RED,
					channel, 5);
			return;
		}
		try {
			Integer userlimit = Integer.parseInt(args[2]);
			PrivateVoiceManager.userLimit(m.getGuild(), m, userlimit);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", gib bitte eine Nutzeranzahl an!", Color.RED,
					channel, 5);
		}
	}
}