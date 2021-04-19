package de.life.commands;

import java.awt.Color;
import java.io.IOException;

import de.life.GlobalVariables;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;

public class IconUpdateCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		if (m.getUser().getIdLong() == GlobalVariables.userIDFelix || m.getUser().getIdLong() == GlobalVariables.userIDLinus) {

			if (message.getAttachments().isEmpty())
				return;

			Attachment attachment = message.getAttachments().get(0);
			try {
				Icon icon = Icon.from(attachment.downloadToFile().join());
				m.getGuild().getJDA().getSelfUser().getManager().setAvatar(icon).queue();

				EmbedMessageBuilder.sendMessage("Icon", "Der Bot Avatar wurde erfolgreich geändert", Color.GREEN,
						channel, 10);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			EmbedMessageBuilder.sendMessage("Icon", "Dazu hast du nicht die Berechtigung", Color.RED,
					channel, 10);
		}

	}

}