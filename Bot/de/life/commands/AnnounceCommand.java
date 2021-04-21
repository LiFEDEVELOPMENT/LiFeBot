package de.life.commands;

import java.awt.Color;
import java.util.Arrays;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class AnnounceCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2) {
			EmbedMessageBuilder.sendMessage("Announce", "Bitte gib eine Nachricht an!", Color.RED, channel, 5);
			return;
		}

		args = Arrays.copyOfRange(args, 1, args.length);
		if (message.getMentionedRoles().size() > 0)
			args = Arrays.copyOfRange(args, 1, args.length);

		String announceMessage = String.join(" ", args);

		if (!m.hasPermission(gchannel, Permission.MANAGE_SERVER)) {
			EmbedMessageBuilder.sendMessage("Announce", "Du hast nicht die Berechtigung, etwas zu announcen!",
					"Dir fehlt: Permission.MANAGE_SERVER", Color.RED, channel, 10);
			return;
		}

		if (message.getMentionedRoles().size() > 0) {
			EmbedMessageBuilder.sendMessage("Announce",
					message.getMentionedRoles().get(0).getAsMention() + "\n\n" + announceMessage,
					m.getEffectiveName().toString(), Color.YELLOW, channel);
		} else {
			EmbedMessageBuilder.sendMessage("Announce", announceMessage, m.getEffectiveName().toString(), Color.YELLOW,
					channel);
		}
		
		if (message.getMentionedRoles().size() > 0)
			LogMessanger.sendLog(m.getGuild().getIdLong(), "Announce", m.getAsMention() + " hat etwas announced!\n'"
					+ announceMessage + "'\n" + message.getMentionedRoles().get(0).getAsMention(), Color.CYAN);
		if (message.getMentionedRoles().size() == 0)
			LogMessanger.sendLog(m.getGuild().getIdLong(), "Announce",
					m.getAsMention() + " hat etwas announcend!\n'" + announceMessage + "'", Color.CYAN);
	}
}