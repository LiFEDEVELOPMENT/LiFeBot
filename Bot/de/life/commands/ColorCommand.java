package de.life.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ColorCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());

		if (!m.hasPermission(gchannel, Permission.MESSAGE_EMBED_LINKS)) {
			EmbedMessageBuilder.sendMessage("Color", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MESSAGE_EMBED_LINKS", Color.RED, channel, 10);
			return;
		}

		if (!m.getGuild().getSelfMember().hasPermission(gchannel, Permission.MESSAGE_EMBED_LINKS)) {
			EmbedMessageBuilder.sendMessage("Color", "Dazu hat der Bot leider nicht die Berechtigung",
					"Ihm fehlt: Permission.MESSAGE_EMBED_LINKS", Color.RED, channel, 10);
			return;
		}

		if (args.length < 2) {
			EmbedMessageBuilder.sendMessage("Color", "Bitte gib eine Farbe im Format #RRGGBB an", Color.RED, channel,
					10);
			return;
		}

		Color color = null;

		try {
			color = Color.decode(args[1]);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Color", "Bitte gib eine Farbe im Format #RRGGBB an", Color.RED, channel,
					10);
			return;
		}

		EmbedBuilder colorBuilder = new EmbedBuilder();
		colorBuilder.setColor(color);
		colorBuilder.setThumbnail("https://singlecolorimage.com/get/" + args[1].substring(1) + "/400x400");
		channel.sendMessage(colorBuilder.build()).queue();
	}
}