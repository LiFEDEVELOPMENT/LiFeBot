package de.life.commands;

import java.util.concurrent.TimeUnit;

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
			channel.sendMessage("Bitte gib eine Message-ID und mindestens einen Emoji an").complete().delete()
					.queueAfter(5, TimeUnit.SECONDS);

			return;
		}

		if (args[2].equalsIgnoreCase("abstimmung")) {
			channel.retrieveMessageById(args[1]).complete().addReaction("✅").queue();
			channel.retrieveMessageById(args[1]).complete().addReaction("�?�").queue();
			message.delete().queue();

			return;
		}

		try {
			channel.retrieveMessageById(args[1]).complete().addReaction(args[2]).queue();
		} catch (Exception e) {
			channel.sendMessage("Bitte gib eine Message-ID und mindestens einen Emoji an").complete().delete()
					.queueAfter(5, TimeUnit.SECONDS);
			e.printStackTrace();
		}

	}

}