package de.life.commands;

import java.awt.Color;

import de.life.classes.DeleteMessages;
import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ClearCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2) {
			EmbedMessageBuilder.sendMessage("Clear", "Bitte gib die Anzahl an Nachrichten an", Color.RED, channel, 10);
			return;
		}

		try {
			int amount = Integer.parseInt(args[1]);

			if (!m.hasPermission(gchannel, Permission.MESSAGE_MANAGE)) {
				EmbedMessageBuilder.sendMessage("Clear", "Dazu hast du nicht die Berechtigung",
						"Dir fehlt: Permission.MESSAGE_MANAGE", Color.RED, channel, 10);

				if (amount == 1)
					LogMessanger.sendLog(m.getGuild().getIdLong(), "#" + channel.getName(),
							m.getAsMention() + " wollte eine Nachrichte löschen!", Color.CYAN);
				if (amount > 1)
					LogMessanger.sendLog(m.getGuild().getIdLong(), "#" + channel.getName(),
							m.getAsMention() + " wollte " + amount + "Nachrichten löschen!", Color.CYAN);

				return;
			}

			if (message.getMentionedMembers().size() == 0) {
				DeleteMessages.delete(channel, amount + 1);
			} else {
				for (Member member : message.getMentionedMembers()) {
					DeleteMessages.delete(channel, amount + 1, member);
				}
			}

			if (amount == 1) {
				EmbedMessageBuilder.sendMessage("Erledigt! - " + amount + " Nachrichten gelöscht.", channel, 5);
			} else {
				EmbedMessageBuilder.sendMessage("Erledigt! - " + amount + " Nachrichten gelöscht.", channel, 5);
			}

			if (amount == 1)
				LogMessanger.sendLog(m.getGuild().getIdLong(), "#" + channel.getName() + ":",
						m.getAsMention() + " hat eine Nachricht gelöscht!");
			if (amount > 1)
				LogMessanger.sendLog(m.getGuild().getIdLong(), "#" + channel.getName() + ":",
						m.getAsMention() + " hat " + amount + " Nachrichten gelöscht!");

		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Clear", "Das war keine Zahl ^^", Color.RED, channel, 10);
			e.printStackTrace();
		}
	}
}