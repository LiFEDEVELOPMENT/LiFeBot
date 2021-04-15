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

		try {
			EmbedBuilder colorBuilder = new EmbedBuilder();
			colorBuilder.setColor(Color.decode(message.getContentDisplay().substring(7, 14)));
			colorBuilder.setDescription("Diese Box hat die Farbe " + message.getContentDisplay().substring(7, 14));
			colorBuilder.setFooter("Angefragt von " + m.getUser().getAsTag());
			channel.sendMessage(colorBuilder.build()).queue();
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			EmbedMessageBuilder.sendMessage("Color", "Bitte gib eine Farbe im Format #RRGGBB an", Color.RED, channel,
					10);
			e.printStackTrace();
		}
	}
}