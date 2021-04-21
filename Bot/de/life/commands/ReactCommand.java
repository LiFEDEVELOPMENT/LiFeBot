package de.life.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ReactCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 3) {
			EmbedMessageBuilder.sendMessage("React", "Bitte gib eine Message-ID und mindestens einen Emoji an",
					Color.RED, channel, 10);
			return;
		}

		if (args[2].equalsIgnoreCase("abstimmung")) {
			channel.retrieveMessageById(args[1]).complete().addReaction("U+2705").queue();
			channel.retrieveMessageById(args[1]).complete().addReaction("U+274C").queue();
			return;
		}

		try {
			channel.retrieveMessageById(args[1]).complete().addReaction(args[2]).queue();
		} catch (Exception e) {
			EmbedMessageBuilder.sendMessage("React", "Bitte gib eine Message-ID und mindestens einen Emoji an",
					Color.RED, channel, 10);
			e.printStackTrace();
		}
	}
}