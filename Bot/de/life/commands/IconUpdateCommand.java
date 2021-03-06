package de.life.commands;

import java.awt.Color;
import java.io.IOException;

import de.life.GlobalVariables;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class IconUpdateCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		if ((m.getUser().getIdLong() != GlobalVariables.userIDFelix
				&& m.getUser().getIdLong() != GlobalVariables.userIDLinus) || message.getAttachments().isEmpty())
			return;

		try {
			Icon icon = Icon.from(message.getAttachments().get(0).downloadToFile().join());
			m.getGuild().getJDA().getSelfUser().getManager().setAvatar(icon).queue();

			EmbedMessageBuilder.sendMessage("Icon", "Der Bot Avatar wurde erfolgreich ge?ndert", Color.GREEN, channel,
					10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}